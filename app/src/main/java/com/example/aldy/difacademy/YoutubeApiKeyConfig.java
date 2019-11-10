package com.example.aldy.difacademy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeApiKeyConfig {
    public static final String YOUTUBE_API_KEY = "AIzaSyCEQRX6WCtt2XP2CtGGd1N1BaShd_vaUqg";
    public static final String YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3/videos/";

    public static String getYoutubeVideoDetailUrl(String youtubeVideoLink) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youtubeVideoLink);
        if (matcher.find()) {
            return "https://www.googleapis.com/youtube/v3/videos/"
                    + "?part=snippet"
                    + "&fields=items(snippet(title,description,thumbnails))"
                    + "&id=" + matcher.group()
                    + "&key=" + YOUTUBE_API_KEY;
        } else {
            return "";
        }
    }

    public static String getYoutubeVideoId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }
    }

    /*
    HASIL JSON

   {
     "items": [
      {
       "snippet": {
        "title": "*VIDEOS YOU NEED TO SEE*",
        "description": "",
        "thumbnails": {
         "default": {
          "url": "https://i.ytimg.com/vi/sg_XFT4K3N4/default.jpg",
          "width": 120,
          "height": 90
         },
         "medium": {
          "url": "https://i.ytimg.com/vi/sg_XFT4K3N4/mqdefault.jpg",
          "width": 320,
          "height": 180
         },
         "high": {
          "url": "https://i.ytimg.com/vi/sg_XFT4K3N4/hqdefault.jpg",
          "width": 480,
          "height": 360
         },
         "standard": {
          "url": "https://i.ytimg.com/vi/sg_XFT4K3N4/sddefault.jpg",
          "width": 640,
          "height": 480
         },
         "maxres": {
          "url": "https://i.ytimg.com/vi/sg_XFT4K3N4/maxresdefault.jpg",
          "width": 1280,
          "height": 720
         }
        }
       }
      }
     ]
    }
    */

}
