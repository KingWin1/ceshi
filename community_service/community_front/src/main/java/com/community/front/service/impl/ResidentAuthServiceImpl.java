package com.community.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.common.dto.ResidentAddDTO;
import com.community.common.exception.BusinessException;
import com.community.common.mapper.ResidentMapper;
import com.community.common.pojo.Resident;
import com.community.common.pojo.ResidentWx;
import com.community.common.service.ResidentService;
import com.community.common.utils.JwtUtil;
import com.community.front.dto.PasswordLoginDTO;
import com.community.front.dto.RegisterDTO;
import com.community.front.dto.SmsLoginDTO;
import com.community.front.dto.WxBindDTO;
import com.community.front.dto.WxLoginDTO;
import com.community.front.mapper.ResidentWxMapper;
import com.community.front.service.ResidentAuthService;
import com.community.front.utils.SmsCodeUtil;
import com.community.front.utils.WxApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 前台居民认证Service实现类
 */
@Service
public class ResidentAuthServiceImpl implements ResidentAuthService {

    /** 手机号正则：11位，1开头，第二位3-9 */
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    @Autowired
    private ResidentMapper residentMapper;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private ResidentWxMapper residentWxMapper;

    @Autowired
    private SmsCodeUtil smsCodeUtil;

    @Autowired
    private WxApiUtil wxApiUtil;

    @Override
    public Map<String, Object> sendSmsCode(String phone, Integer type) {
        checkPhone(phone);
        // 注册场景要求手机号未被注册
        if (type != null && type == 1) {
            Resident exist = selectByPhone(phone);
            if (exist != null) {
                throw new BusinessException("该手机号已注册，请直接登录");
            }
        }
        // 找回密码场景要求手机号已注册
        if (type != null && type == 3) {
            Resident exist = selectByPhone(phone);
            if (exist == null) {
                throw new BusinessException("该手机号未注册");
            }
        }
        String code = smsCodeUtil.sendCode(phone);
        // 开发环境回显验证码，方便前端直接填入
        Map<String, Object> result = new HashMap<>();
        result.put("devCode", code);
        return result;
    }

    @Override
    public boolean verifySmsCode(String phone, String code) {
        return smsCodeUtil.verifyCode(phone, code);
    }

    @Override
    public Map<String, Object> loginBySms(SmsLoginDTO smsLoginDTO) {
        checkPhone(smsLoginDTO.getPhone());
        if (!smsCodeUtil.verifyCode(smsLoginDTO.getPhone(), smsLoginDTO.getCode())) {
            throw new BusinessException("验证码错误或已失效");
        }
        Resident resident = selectByPhone(smsLoginDTO.getPhone());
        if (resident == null) {
            throw new BusinessException("登录失败，该手机号未注册");
        }
        checkStatus(resident);
        return buildLoginResult(resident);
    }

    @Override
    public Map<String, Object> loginByPassword(PasswordLoginDTO loginDTO) {
        checkPhone(loginDTO.getPhone());
        if (!StringUtils.hasText(loginDTO.getPassword())) {
            throw new BusinessException("请输入密码");
        }
        Resident resident = selectByPhone(loginDTO.getPhone());
        if (resident == null) {
            throw new BusinessException("登录失败，该手机号未注册");
        }
        // 密码为空说明该居民从未设置过密码（后台录入的）
        if (!StringUtils.hasText(resident.getPassword())) {
            throw new BusinessException("该账号未设置密码，请使用验证码登录");
        }
        if (!resident.getPassword().equals(loginDTO.getPassword())) {
            throw new BusinessException("手机号或密码错误");
        }
        checkStatus(resident);
        return buildLoginResult(resident);
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        checkPhone(registerDTO.getPhone());
        checkPassword(registerDTO.getPassword());
        // 验证码已由前端"下一步"环节调用 verifySmsCode 校验并消费
        if (selectByPhone(registerDTO.getPhone()) != null) {
            throw new BusinessException("该手机号已注册，请直接登录");
        }
        // 复用公共模块的添加居民接口（与后台居民添加同一业务逻辑）
        ResidentAddDTO addDTO = new ResidentAddDTO();
        addDTO.setName(registerDTO.getPhone());
        addDTO.setPhone(registerDTO.getPhone());
        addDTO.setPassword(registerDTO.getPassword());
        addDTO.setType(3);
        addDTO.setRegisterWay(1);
        addDTO.setGender(2);
        addDTO.setStatus(1);
        residentService.addResident(addDTO);
    }

    @Override
    public void resetPassword(RegisterDTO registerDTO) {
        checkPhone(registerDTO.getPhone());
        checkPassword(registerDTO.getPassword());
        if (!smsCodeUtil.verifyCode(registerDTO.getPhone(), registerDTO.getCode())) {
            throw new BusinessException("验证码错误或已失效");
        }
        Resident resident = selectByPhone(registerDTO.getPhone());
        if (resident == null) {
            throw new BusinessException("该手机号未注册");
        }
        // 仅更新密码字段
        Resident update = new Resident();
        update.setResidentId(resident.getResidentId());
        update.setPassword(registerDTO.getPassword());
        residentMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> wxLogin(WxLoginDTO wxLoginDTO) {
        if (!StringUtils.hasText(wxLoginDTO.getCode())) {
            throw new BusinessException("登录凭证不能为空");
        }
        // 1. code 换取 openid
        String openid = wxApiUtil.getOpenid(wxLoginDTO.getCode());

        // 2. 查询绑定关系
        LambdaQueryWrapper<ResidentWx> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResidentWx::getOpenid, openid);
        ResidentWx residentWx = residentWxMapper.selectOne(wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("openid", openid);

        if (residentWx == null) {
            // 3. 首次进入：落库身份记录，待绑定
            ResidentWx wx = new ResidentWx();
            wx.setOpenid(openid);
            wx.setResidentId(null);
            residentWxMapper.insert(wx);
            result.put("needBind", true);
            return result;
        }
        if (residentWx.getResidentId() == null) {
            // 已记录身份但未绑定居民
            result.put("needBind", true);
            return result;
        }
        // 4. 已绑定：签发token
        Resident resident = residentMapper.selectById(residentWx.getResidentId());
        if (resident == null || resident.getStatus() != 1) {
            result.put("needBind", true);
            return result;
        }
        result.put("needBind", false);
        result.putAll(buildLoginResult(resident));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> wxBind(WxBindDTO wxBindDTO) {
        checkPhone(wxBindDTO.getPhone());
        if (!StringUtils.hasText(wxBindDTO.getOpenid())) {
            throw new BusinessException("缺少微信身份标识，请重新进入小程序");
        }
        if (!smsCodeUtil.verifyCode(wxBindDTO.getPhone(), wxBindDTO.getCode())) {
            throw new BusinessException("验证码错误或已失效");
        }
        Resident resident = selectByPhone(wxBindDTO.getPhone());
        if (resident == null) {
            throw new BusinessException("该手机号未注册，请先注册或进行房屋认证");
        }
        checkStatus(resident);

        // 回填绑定关系（同一openid重复绑定时更新为最新居民）
        LambdaQueryWrapper<ResidentWx> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResidentWx::getOpenid, wxBindDTO.getOpenid());
        ResidentWx residentWx = residentWxMapper.selectOne(wrapper);
        if (residentWx == null) {
            residentWx = new ResidentWx();
            residentWx.setOpenid(wxBindDTO.getOpenid());
            residentWx.setResidentId(resident.getResidentId());
            residentWxMapper.insert(residentWx);
        } else {
            residentWx.setResidentId(resident.getResidentId());
            residentWxMapper.updateById(residentWx);
        }
        return buildLoginResult(resident);
    }

    /**
     * 封装登录结果：居民信息（去除密码）+ token
     */
    private Map<String, Object> buildLoginResult(Resident resident) {
        String token = JwtUtil.generateToken("resident:" + resident.getResidentId());
        resident.setPassword(null);
        Map<String, Object> result = new HashMap<>();
        result.put("resident", resident);
        result.put("token", token);
        return result;
    }

    /**
     * 根据手机号查询居民
     */
    private Resident selectByPhone(String phone) {
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resident::getPhone, phone);
        wrapper.last("limit 1");
        return residentMapper.selectOne(wrapper);
    }

    /**
     * 校验手机号格式
     */
    private void checkPhone(String phone) {
        if (!StringUtils.hasText(phone) || !phone.matches(PHONE_REGEX)) {
            throw new BusinessException("请输入正确的手机号");
        }
    }

    /**
     * 校验密码长度
     */
    private void checkPassword(String password) {
        if (!StringUtils.hasText(password) || password.length() < 6) {
            throw new BusinessException("密码不能少于6位");
        }
    }

    /**
     * 校验居民状态
     */
    private void checkStatus(Resident resident) {
        if (resident.getStatus() != null && resident.getStatus() != 1) {
            throw new BusinessException("账号已被停用，请联系物业");
        }
    }
}
