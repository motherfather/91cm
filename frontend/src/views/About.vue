<template>
  <div @contextmenu="show">
    <h1>번역기</h1>
    <v-textarea
      v-model="text"
      background-color="grey lighten-2"
      color="cyan"
      label="Label"
    ></v-textarea>
    <v-textarea
      v-model="text2"
      background-color="grey lighten-2"
      color="cyan"
      label="Label"
    ></v-textarea>
    <v-btn @click="translate">번역</v-btn>
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
      translate: function () {
        this.text2 = ''
        this.$http.post('/api/user/test', {
          query: this.text
        })
          .then(res => {
            console.log(res.data)
            res.data.translated_text.forEach(text=>{
                this.text2+=text
            })
          })
      }
    }
  }

</script>
<style scoped>

</style>
