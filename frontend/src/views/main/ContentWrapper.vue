<template>
  <main class="mainwrapper" style="height: calc(100vh - 91px);">
    <div class="h-inherit" v-cloak @drop.prevent="dropFile" @dragover.prevent>
      <ul class="c-c-wrapper list-unstyled" @scroll="scrollEvt">
        <div v-for="msg in msgArray" :key="msg.id">
          <MsgBox v-if="msg.message_type=='message'|| msg.message_type=='file'" :msg="msg"
                  @scrollToEnd="scrollToEnd"
                  @imgLoad="imgLoad"></MsgBox>
          <div v-if="msg.message_type=='action'" class="hori-align">
            <v-chip class="ma-2" style="font-weight:bold;">
              {{msg.content}}
            </v-chip>
          </div>
          <div class="date-divider" v-if="msg.message_type=='date'">
            <span class="mydate">{{msg.content}}</span>
          </div>
        </div>
      </ul>
      <a v-if="msgPreviewBool && !isRoot()" @click="clickMsgPreview">
        <div id="c-c-preview" v-bind:class="{active: $store.state.isLActive}">
          <div class="p-wrapper">
            <div>{{ previewObj.username }} : &nbsp;</div>
            <div class="p-nowrap" v-html="previewObj.content"></div>
          </div>
        </div>
      </a>
      <v-row align="center" justify="center" class="c-i-wrapper" v-if="!isRoot()">
        <div class="myflex-column myflex-grow">
          <div style="position: relative;display: flex;">
            <div class="mytextarea-wrapper" v-if="!$store.state.isInviteMode && !$store.state.isSearchMode">
              <v-icon class="my-mail" v-bind:class="{'active-m': sendMail}" @click="sendMailToggle">mail</v-icon>
              <v-icon class="my-search" @click="toggleSearchMode">find_in_page</v-icon>
              <i class="im im-users myfile-upload" style="right: 50px;" @click="inviteToggle"></i>
              <label for="file-input" style="display: block;margin-bottom: 0;">
                <i class="im im-cloud-upload myfile-upload"></i>
              </label>
              <input id="file-input" type="file" ref="fileInput" multiple @change="attachFile" hidden/>
              <b-form-textarea
                class="mytextarea"
                autofocus
                id="textarea-no-resize"
                placeholder="Enter chat message"
                rows="2"
                no-resize
                v-model="message.content"
                @keydown.ctrl.shift.70="toggleSearchMode"
                @keydown.enter.exact="sendMessage($event)"
                @keyup="byteCheck"
                @keydown.shift.alt.50='inviteToggle'
              ></b-form-textarea>
            </div>
            <!--  초대 모드 시작 -->
            <InviteInput @sendMessage="sendMessage" @inviteToggle="inviteToggle"
                         v-if="$store.state.isInviteMode"></InviteInput>
            <!-- 초대 모드 끝  -->
            <!-- 채팅 검색 모드 시작 -->
            <SearchInput
              v-if="$store.state.isSearchMode"
              :cursorPoint="cursorPoint"
              :wrapperEl="wrapperEl"
              @selectMessageList="selectMessageList">
            </SearchInput>
            <!-- 채팅 검색 모드 끝 -->
            <div class="verti-align">
              <v-btn class="mx-2" fab dark large color="cyan"
                     v-if="!$store.state.isInviteMode && !$store.state.isSearchMode" @click="sendMessage($event)">
                <i class="im im-paperplane"></i>
              </v-btn>
            </div>
          </div>
          <div style="display: flex;flex-grow: 1;">
            <!-- 파일 업로드 progress bar -->
            <v-progress-linear
              v-if="isFileUpload"
              color="cyan darken-4"
              height="10"
              v-model="progressValue"
              striped
            ></v-progress-linear>
            <!--            <span style="position: absolute;right: 108px;"> {{ stringByteLength }} / 30000Byte</span>-->
          </div>
        </div>
        <!--        일반 채팅 모드 일때 아이콘-->
        <!--        화상 채팅 모드 일때 아이콘-->
        <!-- <v-btn class="mx-2" fab dark small color="cyan" style="margin-bottom: 25px;"
               v-else @click="sendMessage($event)">
          <i class="im im-paperplane"></i>
        </v-btn> -->
      </v-row>
    </div>
  </main>
</template>
<script>
  import MsgBox from './MsgBox'
  import CommonClass from '../../service/common'
  import SearchInput from './SearchInput'
  import InviteInput from "../../components/InviteInput";

  export default {
    name: 'ContentWrapper',
    components: {
      InviteInput,
      MsgBox, SearchInput
    },
    data() {
      return {
        isFileUpload: false,
        progressValue: 0,
        sendMail: false,
        tempImg: '',
        stringByteLength: 0,
        previewObj: {
          content: '',
          username: ''
        },
        msgPreviewBool: false,
        selectedUserEmail: '',
      }
    },
    mounted() {
      this.$nextTick(() => {
        this.$store.commit('setWrapperEl', document.querySelector('.c-c-wrapper'))
        window.addEventListener('resize', this.widthCheck);
      })
      this.$eventBus.$on('leaveChannelMsg', (user) => {
        this.$store.state.message.content = user.name + '님이 ' + (this.isMine(user) ? "나가셨습니다." : "추방되었습니다.")
        this.sendMessage(null, true)
      })
    },
    updated() {
      this.scrollToEnd()
    },
    activated() {
      if (this.$store.state.oldComponent != 'main' && this.$store.state.selectComponent == 'main') {
        this.scrollToEnd(true)
      }
      this.$store.state.isInviteMode = false
      this.$store.state.isSearchMode = false
    },
    methods: {
      imgLoad(e) {
        this.$store.state.oldScrollHeight = this.wrapperEl.scrollHeight
        // 스크롤을 올리고 있을 때 이미지가 로드되어서 스크롤이 강제로 하단으로 가는 문제를 해결하기 위해 isUpScroll를 사용함.
        if (!this.msgPreviewBool && this.isGetMsgForImgLoad && !this.isUpScroll) {
          this.scrollToEnd(true)
        }
        
      },
      inviteToggle: function (e) {
        let el = document.querySelector(".menuable__content__active.inviteClass")
        if (this.$store.state.isInviteMode == false) {
          this.$store.state.isInviteMode = !this.$store.state.isInviteMode
        } else {
          if (el == null) {
            this.inviteDataInit()
          }
        }
      },
      inviteDataInit: function () {
        this.message.content = ''
        this.$store.state.isInviteMode = !this.$store.state.isInviteMode
      },
      sendMailToggle() {
        this.sendMail = !this.sendMail
        if (this.sendMail) {
          this.$_alert('지금부터 보내는 메시지는' + this.$store.state.currentChannel.name + ' 채널 사용자들에게 ' + '메일로 보내집니다.')
        }
      },
      toggleSearchMode: function () {
        this.$store.state.isSearchMode = !this.$store.state.isSearchMode
        this.$store.state.isInviteMode = false
      },
      widthCheck() {
        this.$store.state.oldScrollHeight = this.$store.state.wrapperEl.scrollHeight
      },
      dropFile: function (e) {
        this.addFile(e.dataTransfer.files)
      },
      attachFile: function (e) {
        this.addFile(e.target.files)
        this.$refs.fileInput.value = null
      },
      uploadFile(formData) {
        this.isFileUpload = true
        this.$http.post('/api/file/upload', formData,
          {
            headers: {
              'Content-Type': 'multipart/form-data'
            },
            onUploadProgress: event => {
              this.progressValue = Math.round((100 * event.loaded) / event.total);
            }
          }).then(res => {
          this.isFileUpload = false
          
          // 어떤걸 해주기 위해 loadChannelFiles를 해주는지...
          // 어차피 본인도 callback함수가 실행되기 때문에 굳이 또 안해줘도 될듯
          //this.$store.dispatch('loadChannelFiles', this.currentChannel.id)
          //this.currentChannel.send('loadChannelFiles|' + this.currentChannel.id)
          this.scrollToEnd(true)
        }).catch(error => {
          this.isFileUpload = false
          this.progressValue = 0
          this.$_error('폴더는 업로드 할 수 없습니다.')
        })
      },
      addFile: function (uploadFiles) {
        if (uploadFiles[0] == null) {
          return;
        }
        this.progressValue = 0
        const maxUploadSize = 100 * 1024 * 1024;
        let fileSize = 0;
        let formData = new FormData();
        ([...uploadFiles]).forEach(file => {
          if (file.size <= 0) {
            this.$_alert('0byte인 파일은 업로드 할 수 없습니다.')
            return
          }
          formData.append("files", file)
          fileSize += file.size
        });
        if (fileSize >= maxUploadSize) {
          this.$_alert('한번에 보낼 수 있는 파일 용량은 100MB 입니다.')
          return;
        } else if (fileSize <= 0) {
          return;
        }
        formData.append('channel_id', this.$store.state.currentChannel.id)
        formData.append('sender', this.$store.state.currentUser.email)
        formData.append('type', 'file')

        this.uploadFile(formData)

      },

      scrollEvt(e) {
        let element = e.target;
        // ===== 스크롤업됐는지 확인하는 코드 start ======
        let currentScrollPosition = e.srcElement.scrollTop;
        if (currentScrollPosition < this.scrollPosition) {
            this.$store.commit('setIsUpScroll',true)
        }
        this.$store.commit('setScrollPosition',currentScrollPosition)
        //====== end ==================

        //스크롤이 없을때에도 스크롤 위치가 최상단이기 때문에 스크롤이 있는지 없는지 판단해야한다.
        if (element.scrollTop <= 0 && element.scrollHeight != element.clientHeight) {
          if (this.$store.state.cursorPoint.empty == false) {
            this.selectMessageList(this.currentChannel, false)
            //this.getMessage(element)
          }
        } else if (this.isScrollAtEnd(element)) {
          this.msgPreviewBool = false
        }
      },

      scrollToEnd(bool) {
        this.$nextTick(() => {
          if (this.firstLoad) {

            this.$store.state.oldScrollHeight = this.wrapperEl.scrollHeight
          }
          if (this.isScrollAtEnd(this.wrapperEl) || this.firstLoad || bool ||
            ((this.oldScrollHeight == this.wrapperEl.clientHeight) && (this.wrapperEl.scrollHeight > this.wrapperEl.clientHeight))) {

            this.wrapperEl.scrollTop = this.wrapperEl.scrollHeight
            this.$store.commit('setFirstLoad', false)
            this.$store.commit('setOldScrollHeight', this.wrapperEl.scrollHeight);
          }
        })
      },
      isScrollAtEnd(wrapperEl) {
        if (Math.floor(wrapperEl.scrollTop + wrapperEl.clientHeight) == this.oldScrollHeight || Math.round(wrapperEl.scrollTop + wrapperEl.clientHeight) == this.oldScrollHeight || Math.floor(wrapperEl.scrollTop + wrapperEl.clientHeight) == wrapperEl.scrollHeight ||
          Math.round(wrapperEl.scrollTop + wrapperEl.clientHeight) == wrapperEl.scrollHeight) {
          return true
        } else {
          return false
        }
      },
      clickMsgPreview() {
        this.scrollToEnd(true)
        this.msgPreviewBool = false
      },
      byteCheck(e) {
        // v-model을 썼음에도 e.target.value를 사용하는 이유는 한글은 바로 바인딩이 안되기때문에 수동적으로 값들을 message.content에 넣기 위함이다.
        this.message.content = e.target.value
        this.stringByteLength = CommonClass.byteCount(this.message.content)
        if ((47 < e.keyCode && e.keyCode < 112 && e.ctrlKey == false) || (e.keyCode == 13 && e.shiftKey == true) || e.keyCode == 32
          || e.keyCode == 229) {
          CommonClass.byteLimit(this.stringByteLength)
        }
      },
    },
    watch: {
      msgArray: function () {
        // 스크롤을 최상단으로 올려 메시지를 가져올 때 실행되는 것을 막기 위한 if문
        if (this.isGetMsgForPreview) {
           this.$store.commit('setIsGetMsgForPreview',false)
        } else { 
          //메세지 미리보기(preview) 실행
          if (this.wrapperEl == null) {
            this.$store.commit('setWrapperEl', document.querySelector('.c-c-wrapper'))
          }
          if (!this.isScrollAtEnd(this.wrapperEl) && this.msgArray.length > 0) {
            let copymsg = JSON.parse(JSON.stringify(this.msgArray[this.msgArray.length - 1]))
            this.previewObj.content = copymsg.content == null ? "첨부파일" : CommonClass.replacemsgForPreview(copymsg.content)
            this.previewObj.username = this.msgArray[this.msgArray.length - 1].user.name
            this.msgPreviewBool = true
          }
        }
      },
    },
  }
</script>

<style scoped>

  @media only screen and (max-width: 1023px) {
    .wrapper .page-wrap .main-content {
      padding-left: 0px !important;
    }
  }

  .theme--light.v-chip:hover:before {
    opacity: 0;
  }

  .v-chip.v-size--default {
    min-height: 32px;
    height: auto;
  }

  .v-chip {
    white-space: normal;
  }
</style>
