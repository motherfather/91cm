<template>
  <div>
    <v-container>
      <v-card>
        <v-card-title>Channel Info</v-card-title>
        <v-card-text>
          <v-row>
            <v-text-field
              label="channel name"
              disabled
              :value="channel.name"
            ></v-text-field>
          </v-row>
          <v-row>
            <v-text-field
              label="channel date"
              disabled
              :value="$moment(channel.register_date).format('YYYY년 MM월 DD일')"
            ></v-text-field>
          </v-row>
        </v-card-text>
      </v-card>
    </v-container>
  </div>
</template>

<script>
  export default {
    name: "ChannelInfo",
    props:['channel'],
    data(){
      return{
        main: ''
      }
    },
    created() {
      console.log(this.channel)
    }
  }
</script>

<style scoped>

</style>
