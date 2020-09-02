import axios from 'axios'
import store from '../store'

class AboutChannel {

  updateLastAccessDate(currentId, oldId) {
    return axios.put('/api/channel/update/lastaccessdate',
      {
        oldChannelId: oldId,
        currentChannelId: currentId,
        userEmail: store.state.currentUser.email
      }
    )
  }

  updateLastAccessStatus(oldVal, newVal) {
    if (store.state.currentChannel != null) {
      if (oldVal == 'main' && newVal != 'main') {
        this.updateLastAccessDate(store.state.currentChannel.id)
      }
    }
  }
}

export default new AboutChannel()
