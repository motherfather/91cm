import axios from "axios";

export default {
  // 현재 채널의 모든 taskList 가져오기


  loadChannelFiles: async function (context, {fileCursorPoint,isFileDrawer}) {
    await axios.post('/api/file/get/files', fileCursorPoint).then(res => {
      let files = res.data 
      console.log(files,'files')
      if(isFileDrawer){
        if(files.length == 0 ){
          context.state.fileCursorPoint.empty = true  
        }
        context.state.fileCursorPoint.first = false
        context.state.fileCursorPoint.cursorId = files[files.length - 1].id
        console.log(context.state.fileCursorPoint,'context.state.fileCursorPoint')
      }
      context.commit('setChannelFiles', context.state.channelFiles.concat(files))
    }).catch(error => {
    })
  },

  updateTaskBoard: function (context) {
    axios.get('/api/tasklist/get/' + context.state.currentChannel.id)
      .then(res => {
        // CommonClass.replaceText(res.data)
        context.commit('setTaskBoard', res.data);
      }).catch(error => {
      console.error(error)
    })
  },
  // 91cm에 가입한 모든 유저 가져오기
  userListUpdate: function (context) {
    /*
    axios.get('/api/user/list')
      .then(res => {
        context.commit('getUserList', res.data);
      }).catch(error => {
      console.error(error);
    })
    */
  },
  // 채널에 입장하지 않은 모든 유저 가져오기
  inviteUserList: function (context) {
    axios.get('/api/user/invite/' + context.state.currentChannel.id)
      .then(res => {
        context.commit('setInviteUserList', res.data);
      }).catch(error => {
      console.error(error);
    })
  },
  // 현재 로그인 한 유저 가져오기
  initCurrentUser: async function (context) {
    await axios.get('/api/user/info')
      .then(res => {
        context.commit('setCurrentUser', res.data)
      }).catch(error => {
        context.commit('setCurrentUser', 'none')
      })
  },
  resetCurrentUser: function (context) {
    context.commit('resetCurrentUser')
  },
  forceLeaveChannel: function (context) {
    context.dispatch('channelList')
  }
}
