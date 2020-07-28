export default {
  setChannelUsers: function(state,payload){
    state.channelUsers = payload
  },
  setCurrentChannel: function (state, payload) {
    state.currentChannel = payload
  },
  setChannelList: function (state, payload) {
    state.channelList = payload
  },
  setSubscribeList: function (state, payload) {
    state.subscribeList = payload
  },
  getSelectComponent: function (state, payload) {
    state.oldComponent = state.selectComponent
    state.selectComponent = payload
  },
  getUserList: function (state, payload) {
    state.userList = payload
  },
  setCurrentUser: function (state, payload) {
    state.currentUser = payload
  },
  resetCurrentUser: function (state) {
    state.currentUser = {}
  },
  setFocus: function (state, payload) {
    state.isfocus = payload
  },
  setIsLogout: function (state, payload) {
    state.isLogout = payload
  },
  setSearchText: function (state, paylod) {
    state.searchText = paylod
  },
  setTaskBoard: function (state,payload) {
    state.taskBoard = payload
  },
  setSmallWidth: function (state, payload) {
    state.isSmallWidth = payload
  },
  setCreateListActive: function(state,payload){
    state.isCreateListActive = payload
  },
  setMsgArray: function(state,payload){
    state.msgArray = payload
  },
  pushMsg: function(state,payload){
    state.msgArray.push(payload)
  },
  setIsVideoMode: function(state,payload){
    state.isVideoMode = payload
  },
  setInviteUserList: function(state,payload){
    state.inviteUserList = payload
  },
  setWrapperEl: function(state,payload){
    state.wrapperEl = payload
  },
  pushChannelArr:function(state,payload){
    state.channelArr.push(payload)
  },
  setFirstLoad:function(state,payload){
    state.firstLoad = payload
  },
  setOldScrollHeight: function (state, payload) {
    state.oldScrollHeight = payload;
  },
  setMessage:function (state,payload){
    state.message = payload
  },
  setChannelFiles: function(state,payload){
    state.channelFiles = payload
  },
  addChannelFile: function(state,payload){
    // unshift와 같은 동작을 하지만 concat이 unshift보다 더 빠르다고 해서 사용
    state.channelFiles = [payload].concat(state.channelFiles)
  },
  setIsGetMsgForImgLoad:function(state,payload){
    state.isGetMsgForImgLoad = payload
  },
  setIsGetMsgForPreview:function(state,payload){
    state.isGetMsgForPreview = payload
  },
  setScrollPosition:function(state,payload){
    state.scrollPosition = payload
  },
  setIsUpScroll:function(state,payload){
    state.isUpScroll = payload
  },
  setFileCursorPoint:function(state,payload){
    state.fileCursorPoint = payload
  },
  initFileCursorPoint:function(state){
    state.fileCursorPoint.channel_id = 0
    state.fileCursorPoint.first = true
    state.fileCursorPoint.cursorId = 0
    state.fileCursorPoint.empty = false
  },
  setModalTitle: function (state,payload) {
    state.modalTitle = payload
  },
  setChannelTitle: function (state,payload) {
    state.channelTitle = payload
  },
  setChannelMode: function (state, payload) {
    state.channelMode = payload
  },
  setModalTrigger: function (state,payload) {
    state.modalTrigger = payload
  }

}
