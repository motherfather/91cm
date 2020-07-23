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
                <v-row v-for="(url,index) in urlList" :key="index">
                  <v-img :src="url.og_image"></v-img>
                  <p>{{ url.og_title }}</p>
                </v-row>
              </div>
              <div style="display:flex;align-items: flex-end;">
                <div v-if="checkFileType" class="mychat-content">
                  <b-row>
                    <b-col v-for="(file,index) in msg.files" :key="index">
                      <a @click="fileDownload(file)">
                        <div class="hori-align">
                          <b-img :alt="file.original_name" :src="selectImage(file)" @load="$emit('imgLoad',$event)"
                                 style="max-width:100px"></b-img>
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
                  <b-row>
                    <v-img :src="og_image"></v-img>
                  </b-row>
                </div>
                <span style="font-size: 11px; margin:0px 3px; width:53px; ">{{ msg.str_send_date }}</span>
                <a class="verti-align confirmMsgDel" :id="'confirmMsgDel' + msg.id" @click="confirmDelete(msg)">
                  <v-icon style="font-size:16px;">delete_outline</v-icon>
                </a>
              </div>

            </slot>
          </div>
          <!-- 채팅메시지내용끝 -->
        </div>
      </div>
    </li>

    <li class="list-unstyled chat-message msgflex-end" v-else>
      <!-- flex에서 벗어나기 위해 감쌈  -->
      <div @mouseover="showMsgOption(msg.id)" @mouseleave="hideMsgOption(msg.id)">
        <!-- 채팅메세지내용 -->
        <div class="myflex">
          <slot name="m-content">
            <div style="display:flex;align-items: flex-end;">
              <a class="verti-align confirmMsgDel" :id="'confirmMsgDel' + msg.id" @click="confirmDelete(msg)">
                <v-icon style="font-size:16px;">delete_outline</v-icon>
              </a>
              <span style="font-size: 11px; margin:0px 3px; width:53px; ">{{ msg.str_send_date }}</span>
            </div>
            <div v-if="checkMsgType" class="my-message mychat-content">
              <pre v-html="textbyFilter(msg.content)"></pre>
              <v-row v-for="(url,index) in urlList" :key="index">
                <v-img :src="url.og_image"></v-img>
                <p v-model="url.og_title"></p>
              </v-row>
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
        <!-- 채팅메시지내용끝 -->
      </div>
    </li>
  </div>
</template>
<script>
  import CommonClass from "../../service/common";

  export default {
    name: 'MsgBox',
    props: ['msg'],
    data() {
      return {
        isMsgOption: false,
        urlList:[]
      }
    },
    computed: {
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
    methods: {
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
        console.log("textbyFilter>>>>")
        // const tagContentRegexp = new RegExp(/<p(.*?)>(.*?)<\/p>/g);
        const htmlTagRegexp = new RegExp(/(<([^>]+)>)/ig);
        const urlRegexp = /(http(s)?:\/\/|www.)([a-z0-9\w]+\.*)+[a-z0-9]{2,4}([\/a-z0-9-%#?&=\w])+(\.[a-z0-9]{2,4}(\?[\/a-z0-9-%#?&=\w]+)*)*/g
        let result = '';
        if (this.$store.state.searchText == '') {
          let arr = content.match(urlRegexp)
          if (arr != null) {
            content = '<p>' + content + '</p>'
            this.makeUrlThumbnail(arr)
            arr.forEach(contentItem => {
              console.log("textbyFilter>>>>ForEach >>>>>>>>")
              // 아래 코드 한줄은 어떤 용도인지? 에러떠서 주석
              // contentItem = contentItem.replace(htmlTagRegexp, '')
              // 같은 url을 두개 넣으면 에러
              result = "<a style='color: blue' href='" + contentItem + "' target='_blank'>" + contentItem + "</a>"
              content = content.replace(contentItem, result)
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
      makeUrlThumbnail: function(urls) {
        console.log("Call makeURLThumbnail Function >>>")
        debugger
        this.$http.post('/api/message/test',{
          url:urls[0]
        })
          .then(res => {
            console.log(res.data)
            this.urlList.push(res.data)
            debugger
          })
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
