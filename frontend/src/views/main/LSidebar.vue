<template>
  <div class="app-sidebar colored" @mouseover="activeBlock" @mouseleave="activeNone">
    <div class="sidebar-header">
      <a class="header-brand" href="/main">
        <!-- <div class="logo-img">
           <img src="" class="header-brand-img" alt="lavalite">
        </div> -->
        <span class="text">91CM</span>
      </a>
      <button type="button" class="nav-toggle"><i data-toggle="expanded" class="ik ik-toggle-right toggle-icon"></i>
      </button>
      <button id="sidebarClose" class="nav-close"><i class="ik ik-x"></i></button>
    </div>

    <div class="sidebar-content">
      <div class="nav-container">
        <nav id="main-menu-navigation" class="navigation-main">
          <div class="nav-item has-sub open">
            <a href="javascript:void(0)">
              <div style="display: flex;align-items: center;">
                <i class="ik ik-layers"></i><span>Channels</span>
                <div style="flex-grow: 1;display: flex;justify-content: flex-end;" v-if="!isRoot()">
                  <button @click="confirmChannel($event, 'create')"
                          style="margin-right: 5px;display: flex;color: white;">
                    <i class="im im-plus-circle" style="margin-right: 15px;display: flex;"></i>
                  </button>
                </div>
              </div>
            </a>

            <div class="submenu-content" v-on:mouseleave="hiddenChannelDelete()">
              <div v-for="(channel,index) in channelList" :key="channel.id"
                   v-on:mouseover="visibilityChannelDelete(channel.id)"
                   @contextmenu="$refs.menu.show($event,channel,'channel')">
                <a @click="joinChannel(channel)" @dblclick="confirmChannel($event, 'update', channel)"
                   class="menu-item myflex" :class="{ 'active-channel': channel.id == currentChannel.id}">
                  <button class="channelDel" :id="'channelDel' + channel.id"
                          @click="confirmChannel($event, 'delete', channel)"
                          style="margin-left:-15px; display:flex; visibility:hidden" v-if="isAdmin()">
                    <i class="im im-minus-circle" style="font-size:15px; color:black;"></i>
                  </button>
                  <div>{{ channel.name }}</div>
                  <div style="display: flex;justify-content: flex-end;flex-grow: 1;" v-if="channel.count!=0">
                    <span class="badge badge-danger" style="position: inherit;">{{channel.count }}</span>
                  </div>
                </a>
              </div>
            </div>
          </div>

          <div class="nav-item has-sub open">
            <a href="javascript:void(0)" style="display: flex;align-items: center;">
              <i class="ik ik-users"></i>
              <span>Users</span>
              <v-badge style="margin-left: 105px" color="#bcc8d8" overlap left :content="channelUsers.length"
                       v-if="channelUsers.length!=0"></v-badge>
            </a>
            <div class="submenu-content" v-on:mouseleave="hiddenChannelUserDelete()">
              <a v-for="(user, index) in channelUsers" :key="user.email"
                 style="cursor:default;display:flex; padding-left: 15px;" class="menu-item verti-align"
                 v-on:mouseover="visibilityChannelUserDelete(index)" @contextmenu="$refs.menu.show($event,user,'user')">
                <div v-if="user.online">
                  <v-badge bottom color="cyan lighten-1" dot offset-x="10" offset-y="10">
                    <img class="avatar" :src="user.picture">
                  </v-badge>
                </div>
                <div v-else>
                  <img class="avatar" :src="user.picture">
                </div>
                <span style="margin-left:15px;">{{ user.name }}</span>
                <div style="display: flex;justify-content: flex-end;flex-grow: 1;" v-if="isActiveForceLeave(user)">
                  <button class="channelUserDel" :id="'channelUserDel' + index" @click="confirmChannelForceLeave(user)"
                          style="margin-left: -15px; display: flex; visibility:hidden">
                    <i class="im im-minus-circle" style="font-size:15px; color:black;"></i>
                  </button>
                </div>
              </a>
            </div>
          </div>
        </nav>
      </div>
    </div>

    <v-dialog v-model="$store.getters.getModalTrigger" persistent hide-overlay max-width="500px"
              style="min-width: 200px;" @keydown.esc.exact="resetModal">
      <v-card>
        <v-row style="width: 100%; margin-bottom: 0px;" dense>
          <v-col cols="11">
            <v-card-title>
              <span class="headline">{{modalTitle}}</span>
            </v-card-title>
          </v-col>
          <v-col cols="1" align-self="center">
            <v-icon size="30" @click="resetModal">close</v-icon>
          </v-col>
        </v-row>
        <v-divider style="margin: 0 0 0 0"></v-divider>
        <v-card-title>
          <v-text-field label="채널 이름" ref="channelName" required :value="channelTitle"
                        @input="updateTitle($event)"
                        counter="20"
                        @keydown.enter.exact="channelFormSubmit"
                        :error-messages="errorMsg" autocomplete="off"></v-text-field>
        </v-card-title>
        <v-card-actions>
          <v-btn text  @click="resetModal">Cancel</v-btn>
          <v-spacer></v-spacer>
          <v-btn color="info" @click="channelFormSubmit">Ok</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>


    <!--    <b-modal id="channelCU" centered ref="modal" @hidden="resetModal" @ok="confirmChannelExec">-->
    <!--&lt;!&ndash;      <template #modal-title>{{ modalTitle }}</template>&ndash;&gt;-->
    <!--      <v-text-field label="채널 이름" :value="channelTitle" ref="modalInput"-->
    <!--                    :rules="[() => !!channelTitle || '채널 이름이 필요합니다.']" hide-details="auto" @keyup="confirmChannelExec($event)"-->
    <!--                    autofocus @input="updateTitle" required-->
    <!--      ></v-text-field>-->
    <!--&lt;!&ndash;      <b-form-group label="채널 이름" label-for="channel-input" invalid-feedback="채널 이름이 필요합니다.">&ndash;&gt;-->
    <!--&lt;!&ndash;        <b-form-input id="channel-input" :value="channelTitle" @input="updateTitle" @keyup="confirmChannelExec($event)" required autofocus&ndash;&gt;-->
    <!--&lt;!&ndash;                      autocomplete="off"/>&ndash;&gt;-->
    <!--&lt;!&ndash;      </b-form-group>&ndash;&gt;-->
    <!--    </b-modal>-->
    <RightClickMenu ref="menu"></RightClickMenu>
  </div>
</template>
<script>
  import RightClickMenu from "../util/RightClickMenu";
  import {mapGetters} from "vuex";

  export default {
    components: {RightClickMenu},
    props: ['modalObj'],
    computed: {
      ...mapGetters({
        modalTitle: "getModalTitle",
        channelTitle: "getChannelTitle",
        modalTrigger: "getModalTrigger"
      })
    },
    name: 'LSidebar',
    data() {
      return {
        errorMsg: null,
        channelIndex: 0,
        userName: '',
        userEmail: ''
      }
    },
    methods: {
      channelFormSubmit: function (event) {
        this.valueCheck()
        if (this.errorMsg == null) {
          this.confirmChannelExec(event)
        }
      },
      resetModal() {
        this.$store.commit('setModalTrigger', false)
        this.$store.commit('setChannelTitle', null)
        this.errorMsg = null
      },
      confirmChannelExec: function (event) {
        let mode = this.$store.getters.getChannelMode
        if ((event.type == "keydown" && event.key == 'Enter') || (event.type == "click")) {
          if ($.trim(this.channelTitle) != "") {
            this.$nextTick(() => {
              this.resetModal()
            })
            if (mode == "create") {
              this.createChannel(this.channelTitle, this.$store.state.currentUser.email)
            } else if (mode == "update") {
              this.$store.state.currentChannel.name = this.channelTitle
              this.updateChannel(this.currentChannel)
            }
          }
        }
      },
      updateTitle: function (event) {
        this.$store.commit('setChannelTitle', event)
        this.valueCheck()
      },
      valueCheck: function () {
        if (this.channelTitle == '' || this.channelTitle == null) {
          this.errorMsg = "채널 이름을 입력해주세요."
        } else if (this.channelTitle.length > 20) {
          this.errorMsg = "채널 이름의 최대 글자 수는 20글자 입니다."
        } else {
          this.errorMsg = null
        }
      },
      activeBlock: function () {
        this.$nextTick(function () {
          let el = document.querySelector('.wrapper')
          let t = $(".sidebar-content")
          if (el.classList.contains('nav-collapsed')) {
            el.classList.remove('menu-collapsed');
            var e = $(".navigation-main .nav-item.nav-collapsed-open");
            e.children(".submenu-content").hide().slideDown(300, function () {
              $(this).css("display", "")
            }), t.find(".nav-item.active").parents(".nav-item").addClass("open"), e.addClass("open").removeClass("nav-collapsed-open")
          }
        })
      },
      activeNone: function () {
        function a(e, s) {
          e.children(".submenu-content").show().slideUp(200, function () {
            i(this).css("display", ""), i(this).find(".menu-item").removeClass("is-shown"), e.removeClass("open"), s && s()
          })
        }

        let l = $(".wrapper")
        if (l.hasClass("nav-collapsed")) {
          l.addClass("menu-collapsed");
          let s = $(".navigation-main .nav-item.open"),
            a = s.children(".submenu-content");
          s.addClass("nav-collapsed-open"), a.show().slideUp(300, function () {
            $(this).css("display", "")
          }), s.removeClass("open")
        }
      },

    }
  }
</script>
<style scoped>
  /* >>>는 deep selector  */
  >>> .v-badge__badge {
    color: #404E67 !important;
  }

  .active-channel {
    background-color: white;
    color: black !important;
  }

  .avatar {
    color: #4c5667;
    font-weight: 600;
    width: 30px;
    height: 30px;
    line-height: 30px;
    border-radius: 50%;
    -webkit-border-radius: 50%;
    -moz-border-radius: 50%;
    display: inline-block;
    background: #ced4da no-repeat center/cover;
    position: relative;
    vertical-align: bottom;
    font-size: .875rem;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
  }
</style>
