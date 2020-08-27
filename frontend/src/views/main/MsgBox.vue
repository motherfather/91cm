<template>
  <div>
    <li class="list-unstyled chat-message" v-if="!isMsgByLoginUser">
      <div class="myflex" @mouseover="showMsgOption(msg.id)" @mouseleave="hideMsgOption(msg.id)">
        <div class="icon">
          <slot name="m-icon">
            <img class="icon-round" :src="msg.user.picture" width="40" height="40"/>
          </slot>
        </div>
        <!-- flex에서 벗어나기 위해 감쌈  -->
        <div>
          <div class="verti-align">
            <slot name="m-info">
              <strong>{{ msg.user.name }}</strong>
            </slot>
          </div>
          <!-- 채팅메세지내용 -->
          <div class="myflex">
            <slot name="m-content">
              <div v-if="checkMsgType"
                   class="mychat-content">
                <pre v-html="textbyFilter(msg.content)"></pre>
              </div>
              <div style="display:flex;align-items: flex-end;">
                <div v-if="checkFileType" class="mychat-content">
                  <b-row>
                    <b-col v-for="(file,index) in msg.files" :key="index">
                      <a @click="fileDownload(file)">
                        <div class="hori-align">
                          <b-img :alt="file.original_name" :src="selectImage(file)" @load="$emit('imgLoad',$event)"
                                 style="max-width:100px; max-height:100px;"></b-img>
                        </div>
                        <p class="file-name"><b>{{file.original_name}}</b></p>
                        <p style="margin:0px;">file size : {{(file.file_size /
                          1024).toLocaleString(undefined,{minimumFractionDigits:2})}}
                          kb</p>
                      </a>
                    </b-col>
                  </b-row>
                </div>
                <div v-if="msg.message_type == 'url'" class="mychat-content">
                </div>
                <span style="font-size: 11px; margin:0px 3px; width:53px; ">{{ msg.str_send_date }}</span>
                <a class="verti-align confirmMsgDel" :id="'confirmMsgDel' + msg.id" @click="confirmDelete(msg)">
                  <v-icon style="font-size:16px;">delete_outline</v-icon>
                </a>
              </div>

            </slot>
          </div>
          <div class="msg-og-container-l" v-if="urlList.length>0">
            <v-card class="msg-og-cardsize" @click="windowOpen()">
              <v-img :src="urlList[0].og_image" class="msg-og-imgsize"
                     style="height: 100px; width: 15vw;min-width: 150px;max-width: 350px;"/>

              <v-card-text>
                <div style="color: black;" class="txt-ellipsis">{{urlList[0].og_title}}</div>
                <div class="txt-ellipsis">{{urlList[0].og_description}}</div>
              </v-card-text>

            </v-card>
          </div>
          <!-- 채팅메시지내용끝 -->
        </div>
      </div>
    </li>

    <li class="list-unstyled chat-message msgflex-end" v-else>
      <!-- flex에서 벗어나기 위해 감쌈  -->
      <div @mouseover="showMsgOption(msg.id)" @mouseleave="hideMsgOption(msg.id)">
        <!-- 채팅메세지내용 -->
        <div class="myflex msgflex-end">
          <slot name="m-content">
            <div style="display:flex;align-items: flex-end;">
              <a class="verti-align confirmMsgDel" :id="'confirmMsgDel' + msg.id" @click="confirmDelete(msg)">
                <v-icon style="font-size:16px;">delete_outline</v-icon>
              </a>
              <span style="font-size: 11px; margin:0px 3px; width:53px; ">{{ msg.str_send_date }}</span>
            </div>

            <div v-if="checkMsgType" class="my-message mychat-content">
              <pre v-html="textbyFilter(msg.content)"></pre>
            </div>
            <div v-if="checkFileType" class="my-message mychat-content">
              <b-row>
                <b-col v-for="(file,index) in msg.files" :key="index">
                  <a @click="fileDownload(file)">
                    <div class="hori-align">
                      <b-img :alt="file.original_name" :src="selectImage(file)" @load="$emit('imgLoad',$event)"
                             style="max-width:100px; max-height:100px;"></b-img>
                    </div>
                    <!-- <b-img thumbnail rounded fluid  alt="이미지를 찾을 수 없습니다."
                           style="max-width: 200px" ></b-img> -->
                    <p class="file-name"><b>{{file.original_name}}</b></p>
                    <p style="margin:0px;">{{formatBytes(file.file_size)}}</p>
                  </a>
                </b-col>
              </b-row>
            </div>
          </slot>
        </div>
        <div class="msg-og-container-r" v-if="getUrlList.length>0">
          <v-card class="msg-og-cardsize" @click="windowOpen()">
            <v-img :src="getUrlList[0].og_image" class="msg-og-imgsize"
                   style="height: 100px; width: 15vw;min-width: 150px;max-width: 350px;" eager
                   @load="$emit('imgLoad',$event)"/>
            <v-card-text>
              <div style="color: black;" class="txt-ellipsis">{{getUrlList[0].og_title}}</div>
              <div class="txt-ellipsis">{{getUrlList[0].og_description}}</div>
            </v-card-text>

          </v-card>
        </div>
        <!-- 채팅메시지내용끝 -->
      </div>
    </li>
  </div>
</template>
<script>
  import CommonClass from "../../service/common";

  const urlRegexp = /(http(s)?:\/\/|www.)([a-z0-9\w]+\.*)+[a-z0-9]{2,4}([\:\/a-z0-9-%#?&=\w])+([\/\.a-z0-9]+(\?[\/a-z0-9-%#?&=\w]+)*)*/g

  export default {
    name: 'MsgBox',
    props: ['msg'],
    data() {
      return {
        isMsgOption: false,
        urlList: []
      }
    },
    computed: {
      getUrlList: function(){
        if (this.msg.delete_yn=='N'){
          return this.urlList
        }else{
          return []
        }
      },
      isMsgByLoginUser: function () {
        return this.msg.sender == this.currentUser.email
      },
      checkMsgType: function () {
        return this.msg.message_type == 'message' || this.msg.delete_yn == 'Y'
          || this.msg.message_type == 'translate' || this.msg.message_type == 'url'

      },
      checkFileType: function () {
        return this.msg.message_type == 'file' && this.msg.delete_yn == 'N'
      },
    },
    created() {
      this.makeUrlThumbnail()
    },
    methods: {
      windowOpen: function () {
        window.open(this.urlList[0].og_url)
      },
      formatBytes: function (byte) {
        return CommonClass.formatBytes(byte)
      },
      showMsgOption: function (msgId) {
        if (this.msg.delete_yn === 'N' && (this.isMsgByLoginUser || this.isAdmin())) {
          this.hideMsgOption()
          $("#confirmMsgDel" + msgId).css("visibility", "visible")
        }
      },
      hideMsgOption: function (msgId) {
        $(".confirmMsgDel").css("visibility", "hidden")
      },
      textbyFilter: function (content) {
        // const tagContentRegexp = new RegExp(/<p(.*?)>(.*?)<\/p>/g);
        // const htmlTagRegexp = new RegExp(/(<([^>]+)>)/ig);
        let result = '';
        if (this.$store.state.searchText == '') {
          let arr = content.match(urlRegexp)
          if (arr != null) {
            content = '<p>' + content + '</p>'
            arr = new Set(arr)
            arr.forEach(contentItem => {

              // 아래 코드 한줄은 어떤 용도인지? 에러떠서 주석
              // contentItem = contentItem.replace(htmlTagRegexp, '')
              // 같은 url을 두개 넣으면 에러
              result = "<a class='msgbox-color' href='" + contentItem + "' target='_blank'>" +
                contentItem + "</a>"
              let replaceItem = contentItem.replace('?', '\\?')
              let replaceRegExp = new RegExp(replaceItem, "g");
              content = content.replace(replaceRegExp, result)
            });
            return content
          } else {
            return content
          }
        }
        return this.$options.filters.highlight(content, this.$store.state.searchText);
      },
      selectImage: function (file) {
        return CommonClass.checkFileType(file)
      },
      fileDownload: function (file) {
        this.$http.get("/api/file/download/" + file.server_name, {
          responseType: 'blob'
        })
          .then(res => {
            const url = window.URL.createObjectURL(new Blob([res.data]))
            const link = document.createElement('a')
            link.href = url;
            link.setAttribute('download', file.original_name)
            document.body.appendChild(link)
            link.click()
            link.remove()
            window.URL.revokeObjectURL(url)
          })
      },
      makeUrlThumbnail: function () {
        let content = this.msg.content
        if (content == null) {
          return
        }
        let arr = content.match(urlRegexp)
        if (arr != null) {
          arr.forEach(urlString => {
            this.$http.post('/api/message/test', {
              url: urlString
            })
              .then(res => {
                if (Object.keys(res.data).length) {
                  this.urlList.push(res.data)
                }
              })
          })
        }
      }
    },
    filters: {
      highlight: function (stringToSearch, searchTerm) {
        if (searchTerm === "") return stringToSearch;
        var iQuery = new RegExp(searchTerm, "ig");
        return stringToSearch
          .toString()
          .replace(iQuery, function (matchedText, a, b) {
            return "<span class='highlight'>" + matchedText + "</span>";
          });
      }
    }
  }
</script>
<style>
.mychat-content > .row > .col> a{
  color:#212529 !important;
}
.msgbox-color{
    color:blue !important;
}
</style>
<style lang="scss" scoped>
  @import "@/assets/css/common.scss";

  .msg-og-container-r {
    @extend .myflex;
    @extend .msgflex-end;
    margin-top: 5px;
  }

  .msg-og-container-l {
    margin-top: 5px;
  }

  .msg-og-cardsize {
    width: 15vw;
    min-width: 150px;
    max-width: 350px;
  }

  .msg-og-imgsize {
    @extend .msg-og-cardsize;
    height: 100px;
  }

</style>
