package com.alphine.team4.carlife.ui.music;

import android.util.Log;

import com.lzx.starrysky.provider.SongInfo;

import java.util.ArrayList;
import java.util.List;

public class MySongList {
    /**
     * privileges : [{"st":0,"flag":68,"subp":1,"fl":128000,"fee":8,"dl":0,"downloadMaxbr":999000,"cp":1,"preSell":false,"cs":false,"toast":false,"playMaxbr":999000,"maxbr":999000,"id":1468998724,"pl":128000,"sp":7,"payed":0}]
     * code : 200
     * playlist : {"privacy":0,"trackNumberUpdateTime":1598540350832,"subscribed":false,"shareCount":0,"adType":0,"trackCount":1,"coverImgId_str":"109951165211313685","specialType":0,"trackIds":[{"at":1598540350832,"v":3,"id":1468998724}],"id":5206108154,"ordered":false,"creator":{"birthday":-2209017600000,"detailDescription":"","backgroundUrl":"http://p1.music.126.net/2zSNIqTcpHL2jIvU6hG0EA==/109951162868128395.jpg","gender":0,"city":210100,"signature":"","description":"","accountStatus":0,"avatarImgId":109951163250233890,"defaultAvatar":true,"avatarImgIdStr":"109951163250233892","backgroundImgIdStr":"109951162868128395","province":210000,"nickname":"强子拉拉","djStatus":0,"avatarUrl":"http://p1.music.126.net/ma8NC_MpYqC-dK_L81FWXQ==/109951163250233892.jpg","authStatus":0,"vipType":0,"followed":false,"userId":3868336055,"mutual":false,"avatarImgId_str":"109951163250233892","authority":0,"userType":0,"backgroundImgId":109951162868128400},"subscribers":[],"opRecommend":false,"highQuality":false,"commentThreadId":"A_PL_0_5206108154","updateTime":1598540350832,"trackUpdateTime":1598540350865,"userId":3868336055,"tracks":[{"no":0,"rt":"","copyright":0,"fee":8,"mst":9,"pst":0,"pop":100,"dt":291666,"rtype":0,"s_id":0,"rtUrls":[],"id":1468998724,"st":0,"cd":"01","publishTime":0,"cf":"","originCoverType":0,"h":{"br":320000,"fid":0,"size":11668845,"vd":-44291},"mv":0,"al":{"picUrl":"http://p4.music.126.net/WrpQtU8HX0QbdNmXk-O0yw==/109951165211313685.jpg","name":"清风徐来 (2020重唱版)","tns":[],"pic_str":"109951165211313685","id":93608465,"pic":109951165211313680},"l":{"br":128000,"fid":0,"size":4667565,"vd":-40023},"m":{"br":192000,"fid":0,"size":7001325,"vd":-41712},"cp":1418001,"alia":[],"djId":0,"ar":[{"name":"张信哲","tns":[],"alias":[],"id":6454}],"ftype":0,"t":0,"v":3,"name":"清风徐来 (2020重唱版)","mark":8192}],"tags":[],"commentCount":0,"titleImage":0,"cloudTrackCount":0,"coverImgUrl":"http://p1.music.126.net/WrpQtU8HX0QbdNmXk-O0yw==/109951165211313685.jpg","playCount":0,"coverImgId":109951165211313680,"createTime":1598537966024,"name":"清新淡雅","backgroundCoverId":0,"subscribedCount":0,"newImported":false,"status":0}
     */

    private int code;
    private PlaylistBean playlist;

    private static final String TAG = "MySongList";
    public List<SongInfo> getSongInfoList(){
        Log.d(TAG, "getSongInfoList: playlist size="+playlist.getTracks().size());
        List<SongInfo> list = new ArrayList<>();
        for (PlaylistBean.TracksBean bean:playlist.getTracks()
        ) {
            SongInfo info = new SongInfo();
            info.setSongId(String.valueOf(bean.getId()));
            info.setSongName(bean.getName());
            info.setDuration(bean.getDt());
            info.setArtist(bean.getAr().get(0).getName());
            info.setSongCover(bean.getAl().getPicUrl());
            list.add(info);
        }
        return list;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public PlaylistBean getPlaylist() {
        return playlist;
    }

    public void setPlaylist(PlaylistBean playlist) {
        this.playlist = playlist;
    }

    public static class PlaylistBean {
        /**
         * privacy : 0
         * trackNumberUpdateTime : 1598540350832
         * subscribed : false
         * shareCount : 0
         * adType : 0
         * trackCount : 1
         * coverImgId_str : 109951165211313685
         * specialType : 0
         * trackIds : [{"at":1598540350832,"v":3,"id":1468998724}]
         * id : 5206108154
         * ordered : false
         * creator : {"birthday":-2209017600000,"detailDescription":"","backgroundUrl":"http://p1.music.126.net/2zSNIqTcpHL2jIvU6hG0EA==/109951162868128395.jpg","gender":0,"city":210100,"signature":"","description":"","accountStatus":0,"avatarImgId":109951163250233890,"defaultAvatar":true,"avatarImgIdStr":"109951163250233892","backgroundImgIdStr":"109951162868128395","province":210000,"nickname":"强子拉拉","djStatus":0,"avatarUrl":"http://p1.music.126.net/ma8NC_MpYqC-dK_L81FWXQ==/109951163250233892.jpg","authStatus":0,"vipType":0,"followed":false,"userId":3868336055,"mutual":false,"avatarImgId_str":"109951163250233892","authority":0,"userType":0,"backgroundImgId":109951162868128400}
         * subscribers : []
         * opRecommend : false
         * highQuality : false
         * commentThreadId : A_PL_0_5206108154
         * updateTime : 1598540350832
         * trackUpdateTime : 1598540350865
         * userId : 3868336055
         * tracks : [{"no":0,"rt":"","copyright":0,"fee":8,"mst":9,"pst":0,"pop":100,"dt":291666,"rtype":0,"s_id":0,"rtUrls":[],"id":1468998724,"st":0,"cd":"01","publishTime":0,"cf":"","originCoverType":0,"h":{"br":320000,"fid":0,"size":11668845,"vd":-44291},"mv":0,"al":{"picUrl":"http://p4.music.126.net/WrpQtU8HX0QbdNmXk-O0yw==/109951165211313685.jpg","name":"清风徐来 (2020重唱版)","tns":[],"pic_str":"109951165211313685","id":93608465,"pic":109951165211313680},"l":{"br":128000,"fid":0,"size":4667565,"vd":-40023},"m":{"br":192000,"fid":0,"size":7001325,"vd":-41712},"cp":1418001,"alia":[],"djId":0,"ar":[{"name":"张信哲","tns":[],"alias":[],"id":6454}],"ftype":0,"t":0,"v":3,"name":"清风徐来 (2020重唱版)","mark":8192}]
         * tags : []
         * commentCount : 0
         * titleImage : 0
         * cloudTrackCount : 0
         * coverImgUrl : http://p1.music.126.net/WrpQtU8HX0QbdNmXk-O0yw==/109951165211313685.jpg
         * playCount : 0
         * coverImgId : 109951165211313680
         * createTime : 1598537966024
         * name : 清新淡雅
         * backgroundCoverId : 0
         * subscribedCount : 0
         * newImported : false
         * status : 0
         */

        private long id;
        private String coverImgUrl;
        private int playCount;
        private String name;
        private List<TracksBean> tracks;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getCoverImgUrl() {
            return coverImgUrl;
        }

        public void setCoverImgUrl(String coverImgUrl) {
            this.coverImgUrl = coverImgUrl;
        }

        public int getPlayCount() {
            return playCount;
        }

        public void setPlayCount(int playCount) {
            this.playCount = playCount;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<TracksBean> getTracks() {
            return tracks;
        }

        public void setTracks(List<TracksBean> tracks) {
            this.tracks = tracks;
        }

        public static class TracksBean {
            /**
             * no : 0
             * rt :
             * copyright : 0
             * fee : 8
             * mst : 9
             * pst : 0
             * pop : 100
             * dt : 291666
             * rtype : 0
             * s_id : 0
             * rtUrls : []
             * id : 1468998724
             * st : 0
             * cd : 01
             * publishTime : 0
             * cf :
             * originCoverType : 0
             * h : {"br":320000,"fid":0,"size":11668845,"vd":-44291}
             * mv : 0
             * al : {"picUrl":"http://p4.music.126.net/WrpQtU8HX0QbdNmXk-O0yw==/109951165211313685.jpg","name":"清风徐来 (2020重唱版)","tns":[],"pic_str":"109951165211313685","id":93608465,"pic":109951165211313680}
             * l : {"br":128000,"fid":0,"size":4667565,"vd":-40023}
             * m : {"br":192000,"fid":0,"size":7001325,"vd":-41712}
             * cp : 1418001
             * alia : []
             * djId : 0
             * ar : [{"name":"张信哲","tns":[],"alias":[],"id":6454}]
             * ftype : 0
             * t : 0
             * v : 3
             * name : 清风徐来 (2020重唱版)
             * mark : 8192
             */

            private int dt;
            private long id;
            private int st;
            private AlBean al;
            private String name;
            private List<ArBean> ar;

            public int getDt() {
                return dt;
            }

            public void setDt(int dt) {
                this.dt = dt;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public int getSt() {
                return st;
            }

            public void setSt(int st) {
                this.st = st;
            }

            public AlBean getAl() {
                return al;
            }

            public void setAl(AlBean al) {
                this.al = al;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<ArBean> getAr() {
                return ar;
            }

            public void setAr(List<ArBean> ar) {
                this.ar = ar;
            }

            public static class AlBean {
                /**
                 * picUrl : http://p4.music.126.net/WrpQtU8HX0QbdNmXk-O0yw==/109951165211313685.jpg
                 * name : 清风徐来 (2020重唱版)
                 * tns : []
                 * pic_str : 109951165211313685
                 * id : 93608465
                 * pic : 109951165211313680
                 */

                private String picUrl;
                private String name;

                public String getPicUrl() {
                    return picUrl;
                }

                public void setPicUrl(String picUrl) {
                    this.picUrl = picUrl;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

            }

            public static class ArBean {
                /**
                 * name : 张信哲
                 * tns : []
                 * alias : []
                 * id : 6454
                 */

                private String name;
                private int id;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

            }
        }
    }

}
