<template>
    <main class="mainwrapper myflex-column" style="height: calc(100vh - 150px);overflow: auto;">
      <div class="container-fluid">
        <div class="page-header">
          <div class="row align-items-end">
            <div class="col-lg-8">
              <div class="page-header-title">
                <i v-if="relatedUser == null" class="ik ik-file-text bg-blue"></i>
                <div class="d-inline">
                  <h5>Profile</h5>
                  <span>개인 프로필</span>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
      <div class="myflex-column myflex-grow">
        <div style="flex-grow: 0.5;"></div>
        <v-row no-gutters>
          <v-col>

              <div class="cetered-align myflex-column" >
                <v-img
                  style="border-radius: 5%;"
                  :src="relatedUser!=null ? relatedUser.picture : getCurrentUser.picture" width="200" height="200"
                ></v-img>
                <h4 style="margin: 20px 0px 20px 0;" >
                  {{relatedUser!=null ? relatedUser.name : getCurrentUser.name}}
                  </h4>
                <!-- <span style="color: #afb5c1;margin-bottom:20px;">부서(IT사업본부) 직급(사원)</span> -->
                <div>
                  <v-card class="user-info-card">
                    <v-card-title class="myflex myflex-nowrap">
                      <v-icon style="display: flex;margin-right: 20px;">mail</v-icon>
                      <p class ="myflex-grow txt-ellipsis" style="margin: 0;">
                        {{relatedUser!=null ? relatedUser.email : getCurrentUser.email}}
                      </p>
                      </v-card-title>
                  </v-card>
                  <v-card class="user-info-card">
                    <v-card-title class="myflex myflex-nowrap">
                      <v-icon style="display: flex;margin-right: 20px;">phone</v-icon>
                      <p class ="myflex-grow txt-ellipsis" style="margin: 0;">
                        {{relatedUser!=null ? relatedUser.phone : getCurrentUser.phone}}
                      </p>
                    </v-card-title>
                  </v-card>
                  <div class="hori-align" v-if="relatedUser==null">
                    <v-btn depressed color="primary" @click="callComponent('edit')">수정</v-btn>
                  </div>
                </div>

              </div>



          </v-col>

        </v-row>

      </div>
    </main>
</template>

<script>

  import {mapGetters} from "vuex";

  export default {
    name: 'UserInfo',
    props: ['relatedUser'],
    computed: {
      ...mapGetters({
        getCurrentUser: 'getCurrentUser',
      })
    },
    created() {
    },
    deactivated() {
    },
    methods: {}
  }
</script>

<style lang="scss" scoped>

  .user-info-card{
    margin-bottom: 25px;
    width: 40vw;
    max-width: 350px;
    min-width: 200px;
  }


</style>
