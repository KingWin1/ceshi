// utils/page.js —— 分页列表通用逻辑（原生 Page 无 Behavior，用实例挂载方式复用）
const DEFAULT_PAGE_SIZE = 10

/**
 * 初始化分页能力：给页面实例挂载 list 状态与 loadMore/refreshList 方法
 * @param {Object} page Page 实例（this）
 * @param {Object} options { fetch(params): Promise<records>, pageSize, mapRow }
 *   fetch 必传，返回当前页数据数组；mapRow 可选，用于逐行加工
 */
function initList(page, options) {
  const pageSize = options.pageSize || DEFAULT_PAGE_SIZE

  /** 加载下一页 */
  page.loadMore = function () {
    if (page.data.loading || page.data.noMore) return
    page.setData({ loading: true })
    options.fetch({ pageNum: page.data.pageNum, pageSize: pageSize }).then(records => {
      const rows = (records || []).map(item => (options.mapRow ? options.mapRow(item) : item))
      page.setData({
        list: page.data.list.concat(rows),
        pageNum: page.data.pageNum + 1,
        noMore: rows.length < pageSize,
        loading: false
      })
    }).catch(() => {
      page.setData({ loading: false })
    })
  }

  /** 重置并加载第一页 */
  page.refreshList = function () {
    page.setData({ list: [], pageNum: 1, noMore: false })
    page.loadMore()
  }
}

module.exports = { initList }
