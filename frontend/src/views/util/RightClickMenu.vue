<template>
  <div>
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
          @click=""
        >
          <v-list-item-title><v-icon>create</v-icon>{{ item.title }}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>

<script>
  import rightClickMetadata from '../../json/rightClickMenuMetadata.json'
  export default {
    name: "RightClickMenu",
    data() {
      return {
        items: [
          {title: 'edit'},
          {title: ''},
          {title: ''},
        ],
        showMenu: false,
        x: 0,
        y: 0
      }
    },
    created() {
      let test = JSON.parse(rightClickMetadata)
    },
    methods: {
      show: function (e, item) {
        e.preventDefault()
        this.showMenu = false
        this.x = e.clientX
        this.y = e.clientY
        this.$nextTick(() => {
          this.showMenu = true
        })
      }
    }
  }
</script>

<style scoped>

</style>
