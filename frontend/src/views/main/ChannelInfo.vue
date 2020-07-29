<template>
  <div>
    <v-container>
      <v-card>
        <v-card-title>Channel Info</v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-text-field
                label="channel name"
                readonly
                color="black"
                :value="channel.name"
              ></v-text-field>
            </v-row>
            <v-row>
              <v-text-field
                label="channel users"
                readonly
                color="black"
                :value="users.length+'명'"
              >
              </v-text-field>
              <!--                <v-layout wrap justify-start >-->
              <!--                  <v-flex>-->
              <!--                    <v-avatar v-for="(user,index) in users"-->
              <!--                              :style="{'margin-left': index+'vw',}">-->
              <!--                      <img :src="user.picture">-->
              <!--                    </v-avatar>-->
              <!--                  </v-flex>-->
              <!--                </v-layout>-->
            </v-row>
            <v-row>
              <v-text-field
                label="channel date"
                readonly
                color="black"
                :value="$moment(channel.register_date).format('YYYY년 MM월 DD일')"
              ></v-text-field>
            </v-row>
          </v-container>
        </v-card-text>
      </v-card>
    </v-container>
  </div>
</template>
<script>
export default {
  name: "ChannelInfo",
  props: ['channel'],
  data() {
    return {
      users: [],
      avatarSize: 48
    }
  },
  created() {
    this.$http.get('/api/user/channel/' + this.channel.id)
      .then(res => {
        this.users = res.data
      }).catch(error => {
      console.error(error)
    })
  },
  methods: {}
}
</script>

<style scoped>

</style>
