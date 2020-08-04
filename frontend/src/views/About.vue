<template>
  <v-container @wheel="test">
    <v-row align="center" justify="center" style="max-height: 100%" ref="row">
      <v-col cols="12">
        <v-card>
          <v-card-title>{{list[index]}}</v-card-title>
          <v-card-text>1111111111111111111111111111111</v-card-text>
          <v-img :src="images[index]" max-height="40vh" contain></v-img>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
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
        list: ['1','2','3'],
        images:[
          'https://images.squarespace-cdn.com/content/v1/5a5906400abd0406785519dd/1552662149940-G6MMFW3JC2J61UBPROJ5/ke17ZwdGBToddI8pDm48kLkXF2pIyv_F2eUT9F60jBl7gQa3H78H3Y0txjaiv_0fDoOvxcdMmMKkDsyUqMSsMWxHk725yiiHCCLfrh8O1z4YTzHvnKhyp6Da-NYroOW3ZGjoBKy3azqku80C789l0iyqMbMesKd95J-X4EagrgU9L3Sa3U8cogeb0tjXbfawd0urKshkc5MgdBeJmALQKw/baelen.jpg?format=1500w',
          'https://www.publicdomainpictures.net/pictures/320000/velka/background-image.png',
          'https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg',
        ],
        index: 0,
      };
    },
    methods: {
      test:function (e){
        this.$refs.row.classList.add('animate__animated','animate__fadeIn')
        this.$refs.row.addEventListener('animationend', e=>{
          console.log(e)
          this.$refs.row.classList.remove('animate__animated','animate__fadeIn')
          this.$refs.row.removeEventListener('animationend', e=>{
            console.log(e)
          })
        })
        this.index = this.values(e.deltaY)
      },
      values: function (val){
        if (val > 0){
          return (this.list.length <= this.index+1)? this.list.length-1 : this.index+1
        }else{
          return (0 > this.index-1)? 0 : this.index-1
        }
      }
    },
  }

</script>
<style scoped>

</style>
