<template>
  <div>
    <v-menu
      v-model="showMenu"
      :position-x="x"
      :position-y="y"
      absolute
      offset-y
    >
      <v-list dense>
        <v-list-item
          v-for="(item, index) in menuList"
          :key="index"
          v-if="authorityCheck(item.auth)"
          @click="callFunction(item.text)"
        >
          <v-list-item-title>
            <v-icon>{{ iconMatch[item.text]}}</v-icon>
            {{ item.text }}
          </v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
    <v-dialog v-model="dialog" max-width="500" hide-overlay >
      <v-row justify="end" align="end"><v-icon @click="closeDialog">close</v-icon></v-row>
      <UserInfo style="background-color: white" v-if="itemType=='user'" :relatedUser="item"></UserInfo>
      <ChannelInfo v-else-if="itemType=='channel'" :channel="item"></ChannelInfo>
    </v-dialog>
  </div>
</template>

<script>
  import rightClickMetadata from '../../json/rightClickMenuMetadata.json'
  import UserInfo from "../user/UserInfo";
  import ChannelInfo from "../main/ChannelInfo";

  export default {
    name: "RightClickMenu",
    components: {ChannelInfo, UserInfo},
    data() {
      return {
        dialog: false,
        event: undefined,
        itemType: undefined,
        iconMatch: rightClickMetadata.iconMatch,
        menuList: {},
        item: undefined,
        showMenu: false,
        x: 0,
        y: 0
      }
    },
    created() {
    },
    mounted() {
    },
    methods: {
      authorityCheck: function (auth){
        if (auth =='user'){
          return this.currentUser.roles.includes('ROLE_USER') || this.isAdmin()
        }
        if (auth == 'admin'){
          return this.isAdmin()
        }
      },
      closeDialog: function (){
        this.dialog = false
        this.itemType = undefined
        this.item = undefined
      },
      callFunction(itemName) {
        this[this.itemType + itemName](this.item)
      },
      show: function (e, item, type) {
        e.preventDefault()
        this.event = e
        this.itemType = undefined
        this.item = undefined
        this.showMenu = false
        this.x = e.clientX
        this.y = e.clientY
        this.$nextTick(() => {
          this.itemType = type
          this.item = item
          this.typeCheck()
          this.showMenu = true
        })
      },
      typeCheck: function () {
        switch (this.itemType) {
          case "channel":
            this.menuList = rightClickMetadata.channel
            return;
          case "user":
            this.menuList = rightClickMetadata.user
            return;
          default:
            return;
        }
      },
      channelEdit: function (channel) {
        // 아래의 현재 채널을 이동하는 코드는 임시코드
        // 현재 채널 정보를 업데이트하는 함수 confirmChannelExec 에서
        // currentChannel를 가지고 업데이트 함
        // 추후 가져온 channel을 confirmChannelExec에 전달한 좋은 방식이 떠올 때까지의 임시코드
        this.$store.commit('setCurrentChannel',channel)
        this.confirmChannel(this.event, 'update', channel)
      },
      channelDelete: function (channel){
        this.confirmChannel(this.event, 'delete', channel)
      },
      channelInfo: function(channel){
        this.dialog = true
      },
      userInfo: function (user) {
        this.dialog = true
        // alert(user.name)
      },
    }
  }
</script>

<style scoped>

</style>
