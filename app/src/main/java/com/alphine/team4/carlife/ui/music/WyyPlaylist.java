package com.alphine.team4.carlife.ui.music;

import java.util.List;

public class WyyPlaylist {
    /**
     * code : 200
     * playlist : [{"privacy":0,"trackNumberUpdateTime":1598538184002,"subscribed":false,"adType":0,"trackCount":6,"specialType":5,"id":5205918734,"totalDuration":0,"ordered":false,"creator":{"birthday":-2209017600000,"detailDescription":"","backgroundUrl":"http://p1.music.126.net/2zSNIqTcpHL2jIvU6hG0EA==/109951162868128395.jpg","gender":0,"city":210100,"signature":"","description":"","accountStatus":0,"avatarImgId":109951163250233890,"defaultAvatar":true,"avatarImgIdStr":"109951163250233892","backgroundImgIdStr":"109951162868128395","province":210000,"nickname":"强子拉拉","djStatus":0,"avatarUrl":"http://p1.music.126.net/ma8NC_MpYqC-dK_L81FWXQ==/109951163250233892.jpg","authStatus":0,"vipType":0,"followed":false,"userId":3868336055,"mutual":false,"avatarImgId_str":"109951163250233892","authority":0,"userType":0,"backgroundImgId":109951162868128400},"subscribers":[],"opRecommend":false,"highQuality":false,"commentThreadId":"A_PL_0_5205918734","updateTime":1598539632223,"trackUpdateTime":1598682636735,"userId":3868336055,"anonimous":false,"tags":[],"titleImage":0,"coverImgUrl":"https://p1.music.126.net/t_XYe-lwEvMPvEN2t1GnTA==/3384296791496290.jpg","cloudTrackCount":0,"playCount":0,"coverImgId":3384296791496290,"createTime":1598536020008,"name":"强子拉拉喜欢的音乐","backgroundCoverId":0,"subscribedCount":0,"newImported":false,"status":0},{"privacy":0,"trackNumberUpdateTime":1598682681110,"subscribed":false,"adType":0,"trackCount":4,"coverImgId_str":"109951164317903340","specialType":0,"id":5206108154,"totalDuration":0,"ordered":false,"creator":{"birthday":-2209017600000,"detailDescription":"","backgroundUrl":"http://p1.music.126.net/2zSNIqTcpHL2jIvU6hG0EA==/109951162868128395.jpg","gender":0,"city":210100,"signature":"","description":"","accountStatus":0,"avatarImgId":109951163250233890,"defaultAvatar":true,"avatarImgIdStr":"109951163250233892","backgroundImgIdStr":"109951162868128395","province":210000,"nickname":"强子拉拉","djStatus":0,"avatarUrl":"http://p1.music.126.net/ma8NC_MpYqC-dK_L81FWXQ==/109951163250233892.jpg","authStatus":0,"vipType":0,"followed":false,"userId":3868336055,"mutual":false,"avatarImgId_str":"109951163250233892","authority":0,"userType":0,"backgroundImgId":109951162868128400},"subscribers":[],"opRecommend":false,"highQuality":false,"commentThreadId":"A_PL_0_5206108154","updateTime":1598682681110,"trackUpdateTime":1598682681110,"userId":3868336055,"anonimous":false,"tags":[],"titleImage":0,"coverImgUrl":"https://p1.music.126.net/fI-ojyZtv5o4KqotJMaO9g==/109951164317903340.jpg","cloudTrackCount":0,"playCount":0,"coverImgId":109951164317903340,"createTime":1598537966024,"name":"清新淡雅","backgroundCoverId":0,"subscribedCount":0,"newImported":false,"status":0}]
     * more : false
     * version : 1598682681095
     */

    private int code;
    private List<PlaylistBean> playlist;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<PlaylistBean> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<PlaylistBean> playlist) {
        this.playlist = playlist;
    }

    public static class PlaylistBean {
        /**
         * privacy : 0
         * trackNumberUpdateTime : 1598538184002
         * subscribed : false
         * adType : 0
         * trackCount : 6
         * specialType : 5
         * id : 5205918734
         * totalDuration : 0
         * ordered : false
         * creator : {"birthday":-2209017600000,"detailDescription":"","backgroundUrl":"http://p1.music.126.net/2zSNIqTcpHL2jIvU6hG0EA==/109951162868128395.jpg","gender":0,"city":210100,"signature":"","description":"","accountStatus":0,"avatarImgId":109951163250233890,"defaultAvatar":true,"avatarImgIdStr":"109951163250233892","backgroundImgIdStr":"109951162868128395","province":210000,"nickname":"强子拉拉","djStatus":0,"avatarUrl":"http://p1.music.126.net/ma8NC_MpYqC-dK_L81FWXQ==/109951163250233892.jpg","authStatus":0,"vipType":0,"followed":false,"userId":3868336055,"mutual":false,"avatarImgId_str":"109951163250233892","authority":0,"userType":0,"backgroundImgId":109951162868128400}
         * subscribers : []
         * opRecommend : false
         * highQuality : false
         * commentThreadId : A_PL_0_5205918734
         * updateTime : 1598539632223
         * trackUpdateTime : 1598682636735
         * userId : 3868336055
         * anonimous : false
         * tags : []
         * titleImage : 0
         * coverImgUrl : https://p1.music.126.net/t_XYe-lwEvMPvEN2t1GnTA==/3384296791496290.jpg
         * cloudTrackCount : 0
         * playCount : 0
         * coverImgId : 3384296791496290
         * createTime : 1598536020008
         * name : 强子拉拉喜欢的音乐
         * backgroundCoverId : 0
         * subscribedCount : 0
         * newImported : false
         * status : 0
         * coverImgId_str : 109951164317903340
         */

        private int trackCount;
        private long id;
        private String name;

        public int getTrackCount() {
            return trackCount;
        }

        public void setTrackCount(int trackCount) {
            this.trackCount = trackCount;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static class CreatorBean {
            /**
             * birthday : -2209017600000
             * detailDescription :
             * backgroundUrl : http://p1.music.126.net/2zSNIqTcpHL2jIvU6hG0EA==/109951162868128395.jpg
             * gender : 0
             * city : 210100
             * signature :
             * description :
             * accountStatus : 0
             * avatarImgId : 109951163250233890
             * defaultAvatar : true
             * avatarImgIdStr : 109951163250233892
             * backgroundImgIdStr : 109951162868128395
             * province : 210000
             * nickname : 强子拉拉
             * djStatus : 0
             * avatarUrl : http://p1.music.126.net/ma8NC_MpYqC-dK_L81FWXQ==/109951163250233892.jpg
             * authStatus : 0
             * vipType : 0
             * followed : false
             * userId : 3868336055
             * mutual : false
             * avatarImgId_str : 109951163250233892
             * authority : 0
             * userType : 0
             * backgroundImgId : 109951162868128400
             */

            private String nickname;

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }
        }
    }
}
