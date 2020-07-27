<template>
  <div @contextmenu="show">
    <div>
      <v-btn @click="test">Click</v-btn>
    </div>
    <v-menu
      v-model="showMenu"
      :position-x="x"
      :position-y="y"
      absolute
      offset-y
    >
      <v-list>
        <v-list-item
          v-for="(item, index) in items"
          :key="index"
          @click="alert(index)"
        >
          <v-list-item-title>{{ item.title }}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>
<script>
  export default {
    name: 'About',
    created() {
    },
    mounted() {

    },
    computed: {},
    data() {
      return {
        showMenu: false,
        text: '',
        text2:'',
        x: 0,
        y: 0,
        items: [
          { title: 'Click Me' },
          { title: 'Click Me' },
          { title: 'Click Me' },
          { title: 'Click Me 2' },
        ],
      };
    },
    watch: {},
    methods: {
      show: function(e){
        e.preventDefault()
        this.showMenu = false
        this.x = e.clientX
        this.y = e.clientY
        this.$nextTick(() => {
          this.showMenu = true
        })
      },
      test: function () {
        this.$http.post("/api/message/test",{
          url: 'https://www.html5rocks.com/ko/tutorials/webrtc/infrastructure/'
        }).then(res=>{
          console.log(res)
        })
      }
    }
  }

</script>
<style scoped>

</style>
