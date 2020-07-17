<template>
  <v-overlay
    :value="overlayShow"
    opacity="0.8"
    dark
    z-index="10000"
  >
    <v-progress-circular indeterminate size="64" v-show="!showFile"></v-progress-circular>
    <v-row justify="center" v-show="showFile" style="flex-wrap: nowrap;">
      <div  class="verti-align" style="margin-top: 10px">
        <v-icon v-if="index > 0" large @click="moveFile(--index)">keyboard_arrow_left</v-icon>
      </div>
      <div >
        <div class="myflex">
          <div style="display:inline-block">
            <v-btn icon @click="fileDownload(selectFile)"><i class="im im-download"></i></v-btn>
            <!--              <v-btn icon ><i class="im im-printer"></i></v-btn>-->
          </div>
          <div class="myflex-grow-end">
            <v-btn icon @click="hide"><i class="im im-x-mark"></i></v-btn>
          </div>
        </div>
        <v-img v-if="isImgFile"
               :src="selectImage(selectFile,'origin')"
               contain
               max-height="60vh" max-width="45vw"
               eager
               @load="showFile = true;"
        ></v-img>
        <div v-if="notPreview" style="margin:15px 0;">
          <div class="hori-align file-font-h4"><v-icon style="margin-right:5px;">info</v-icon>미리보기가 지원되지 않는 형식입니다.</div>
        </div>
        <div style="overflow-y: auto;overflow-x: hidden; width:100%;  height:80vh;" v-if="isPdfFile">
          <pdf
            v-for="page in pages"
            :key="page"
            @progress="loadedRatio = $event"
            :src="pdfSrc"
            :page="page"
            style="width:100%;"
            >
          </pdf>
        </div>
        <div v-if="isTxtFile" class="txtfile-container">
            <div class="txtfile-inner">
              <pre style="color: black;margin: 0px;background-color: white;">{{txtVal}}</pre>  
            </div>
        </div>
      </div>
      <div class="verti-align" style="margin-top: 10px">
        <v-icon v-if="index < channelFiles.length-1" large @click="moveFile(++index)">keyboard_arrow_right</v-icon>
      </div>
    </v-row>
  </v-overlay>
  
  
</template>

<script>
  import pdf from "vue-pdf";
  import axios from 'axios'
  import CommonClass from "../../service/common";

  export default {
    name: "FilePreview",
    components: {
      pdf
    },
    data() {
      return {
        showFile: false,
        selectFile: undefined,
        overlayShow: false,
        pdfSrc: undefined,
        pages: undefined,
        index: 0,
        txtVal:''
      }
    },
    computed:{
      notPreview:function(){
        return !this.isImgFile && !this.isTxtFile && !this.isPdfFile
      },
      isTxtFile:function(){
        if(this.selectFile!==undefined && this.selectFile.extension === 'txt'){
          // 나중에 api 만들면 아래코드 api 호출 후로 변경해야함
          //this.showFile=true
          return true
        }else{
          return false
        }
      },
      isImgFile:function(){
        if(this.selectFile!==undefined){
          let type = this.selectFile.extension
          type = type.toLowerCase().trim()
          switch (type) {
            case ('png'):
            case ('jpg'):
            case ('jpeg'):
            case ('gif'):
              return true
          }
        }else{
          return false
        }
      },
      isPdfFile:function(){
        return this.selectFile!==undefined && this.selectFile.extension === 'pdf'
      }
    },
    methods: {
      clickEvent: function (e) {
        if (this.overlayShow === true) {
          if (e.code === "ArrowRight") {
            this.moveFile(++this.index)
          } else if (e.code === "ArrowLeft") {
            this.moveFile(--this.index)
          } else if (e.code === "Escape") {
            this.hide()
          }
        }
        // else if (this.overlayShow === false) {
        //   if (e.code === "Escape") {
        //     // this.callComponent('main')
        //   }
        // }
      },
      loadTxt:function(file){
        axios.get('/api/file/loadtxt/' + file.server_name).then(res=>{
          this.txtVal = res.data
          this.showFile=true
        })
      },

      show: function (file) {
        document.addEventListener('keydown', this.clickEvent)
        switch(file.extension){
          case ('pdf'):
            this.loadPdfFile(file) 
          case ('txt'):
            this.loadTxt(file)
        }
        if(!this.isImgFile && !this.isTxtFile && !this.isPdfFile){
          this.showFile = true
        }
        this.index = this.channelFiles.findIndex((f) => f.id == file.id)
        this.selectFile = file
        this.overlayShow = true
      },
      hide: function () {
        document.removeEventListener('keydown', this.clickEvent)
        this.overlayShow = false
        this.selectFile = undefined
        this.showFile = false
      },
      moveFile: function (index) {
        if (index < 0) {
          this.index = 0
          return
        } else if (index >= this.channelFiles.length) {
          this.index = this.channelFiles.length - 1
          return;
        }
        this.showFile = false
        if (this.channelFiles[index].extension === 'pdf') {
          this.loadPdfFile(this.channelFiles[index])
        }
        this.selectFile = this.channelFiles[index];
        if(!this.isImgFile && !this.isTxtFile && !this.isPdfFile){
          this.showFile = true
        }
      },
      loadPdfFile(file) {
        this.pdfSrc = pdf.createLoadingTask('/api/file/download/' + file.server_name)
        this.pdfSrc.promise.then(pdf => {
          this.pages = pdf.numPages;
          this.showFile = true
        });
      },
      selectImage: function (file, option) {
        if (this.prevImage === undefined) {
          this.prevImage = CommonClass.checkFileType(file, option)
          return this.prevImage
        } else if (this.prevImage == CommonClass.checkFileType(file, option)) {
          this.showFile = true
          return this.prevImage
        } else {
          this.prevImage = CommonClass.checkFileType(file, option)
          return this.prevImage
        }
      },
      fileDownload: function (file) {
        CommonClass.fileDownload(file)
      },
    }
  }
</script>

<style scoped>
 >>>canvas{
   max-width: 45vw;
 }
</style>
