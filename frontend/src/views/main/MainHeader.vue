<template>
  <header class="header-top" header-theme="light">
    <div class="container-fluid">
      <div class="d-flex justify-content-between">
        <div class="top-menu d-flex align-items-center" style="flex-grow: 1;">
          <button type="button" class="btn-icon mobile-nav-toggle d-lg-none"><span></span></button>
          <div v-if="$store.state.currentChannel!=null" style="font-weight: bold;font-size: 15px;width: 0;white-space: nowrap;text-overflow: ellipsis;overflow: hidden;flex-grow: 1;">
            {{ $store.state.currentChannel.name }}
          </div>
        </div>
        <div class="top-menu d-flex align-items-center">
          <div class="dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="notiDropdown" role="button" data-toggle="dropdown"
               aria-haspopup="true" aria-expanded="false"><i class="ik ik-bell"></i><span class="badge bg-danger"
                                                                                          v-show="getAlarmList.length > 0">{{alarmList.length}}</span></a>
            <div class="dropdown-menu dropdown-menu-right notification-dropdown" aria-labelledby="notiDropdown">
              <h4 class="header">Notifications</h4>
              <div class="notifications-wrap">
                <div v-if="getAlarmList.length==0" class="media nuj hori-align"><span class="heading-font-family media-heading">알림이 없습니다.</span></div>
                <div v-for="(alarm,index) in getAlarmList" :key="index">
                  <a class="media" style="cursor: default;">
                    <span class="media-body">
                      <div class="heading-font-family media-heading">
                        {{getUserNameByEmail(alarm.sender)}} 님이 {{alarm.channel_name}}에 초대했습니다. 수락하시겠습니까?
                      </div>
                      <div class="menulist-header-icon">
                        <b-button style="padding: 0 0.5rem 0 0;" size="sm"
                                  variant="nonoutline" @click="inviteAccept(alarm,index)">
                          <i class="im im-check-mark-circle" style="color: #42b983;"></i>
                        </b-button>
                        <b-button style="padding: 0 0.5rem 0 0;" size="sm" variant="nonoutline"
                                  @click="inviteRefuse(alarm,index)">
                          <i class="im im-x-mark-circle" style="color: red;"></i>
                        </b-button>
                      </div>
                    </span>
                  </a>
                </div>
              </div>
            </div>
          </div>
          <button v-if="$store.state.channelList[0]!=null" type="button" @click="rightSidebarToggle"
                  class="nav-link ml-10 right-sidebar-toggle"><i class="ik ik-message-square"></i></button>
          <div class="dropdown">
            <a class="dropdown-toggle" href="#" id="userDropdown" role="button" data-toggle="dropdown"
               aria-haspopup="true" aria-expanded="false">
              <img class="avatar" :src="$store.state.currentUser.picture">
            </a>
            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">
              <a class="dropdown-item" @click="callComponent('user')"><i class="ik ik-user dropdown-icon"></i> Profile</a>
              <a v-if="false" class="dropdown-item" @click="$router.push('/develop')"><i class="ik ik-settings dropdown-icon"></i> Setting</a>
<!--              <a class="dropdown-item"><i class="ik ik-info dropdown-icon"></i> About</a>-->
              <a class="dropdown-item" v-if="isAdmin()" @click="callComponent('admin')"><i class="ik ik-settings dropdown-icon"></i> Permission</a>
              <!-- info 페이지가 완성되면 if 처리 제거 -->
              <a class="dropdown-item" v-if="false" @click="$router.push('/info')"><i class="ik ik-info dropdown-icon"></i> Info</a>
              <a class="dropdown-item" @click="SignOut"><i class="ik ik-power dropdown-icon"></i> Logout</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script>
  import '../../../dist/js/theme.js'
  import AboutChannel from '../../service/aboutchannel'

  export default {
    name: 'MainHeader',
    data() {
      return {
        alarmList: [],
      }
    },
    computed: {
      getAlarmList: function () {
        while (this.alarmList.length > 5) {
          this.alarmList.pop()
        }
        return this.alarmList
      }
    },
    created() {
      this.subscribe("/sub/alarm/" + this.$store.state.currentUser.email, (e) => {
        let invite = JSON.parse(e.body)
        this.alarmList.unshift(invite)
      }),
        this.$http.get('/api/invite/list')
          .then(res => {
            this.alarmList = res.data.reverse()
            console.log(this.alarmList, "invite list")
          })
          .catch(error => {
          })
    },
    mounted() {

    },
    methods: {
      rightSidebarToggle: function (e) {
        $('.right-sidebar-toggle')[0].classList.toggle('active');
        $('.wrapper').toggleClass('right-sidebar-expand');
        if($('.main-content').hasClass('rsidebar-padding-right')){
          $('.main-content').removeClass('rsidebar-padding-right')
        }else{
          console.log(this,'this')
          console.log(this.fileCursorPoint,'this.fileCursorPoint')
          $('.main-content').addClass('rsidebar-padding-right')
        }
        return false;
      },
      callComponent: function (component) {
        this.$store.commit('getSelectComponent', component)
      }
      ,
      LSidebarToggle: function () {
        this.$store.state.isLActive = !this.$store.state.isLActive
      }
      ,
      RSidebarOpen: function () {
        this.$store.state.isRActive = true
      }
      ,
      SignOut() {
        this.$store.commit('setIsLogout', true)
        window.location.href = "/logout"
      }
      ,
      getUserNameByEmail: function (email) {
        console.log(this.$store.state.userList,'userList')
        let foundEmail = this.$store.state.userList.find(element => {return element.email == email})
        if(foundEmail!=null){
          return foundEmail.name
        }else{
          return null
        }
      }
    }
    ,
  }
</script>

<style>
a.dropdown-item{
  color:#212529 !important;
}

</style>

<style scoped>

  .v-application .ml-10 {
    margin-left: 10px !important;
  }
</style>
