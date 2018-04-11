package com.maclolm.iwaraviewer.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maclolm.iwaraviewer.bean.VideoAddressJson;
import com.maclolm.iwaraviewer.bean.VideoInfo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

public class CrawlTool {
    private static final String DOMAIN = "http://www.iwara.tv";
    private static final String DOMAIN_NSFW = "http://ecchi.iwara.tv";
    private static Gson gson = new Gson();


    public static void getCrawlData(ArrayList<VideoInfo> list, String pageNum) {
        System.out.println("getCrawlData start");
        String url = DOMAIN + "/videos?page=" + pageNum;
        GetListPage(url, "360p", pageNum, list);
    }


    private static void GetListPage(String url, String resolution, String pageNum, ArrayList<VideoInfo> videoInfos) {
        try {
            VideoInfo videoInfo = new VideoInfo();
            Document doc = Jsoup.connect(url).get();

            Elements nodes = doc.getElementsByClass("node-video");
            for (Element node : nodes) {
                //Show like/view rate START
                String like = "0";
                String view = "1";
                double likeD;
                double viewD;
                double rateD;

                Element likeElement = node.selectFirst(".icon-bg .right-icon.likes-icon");
                Element viewElement = node.selectFirst(".icon-bg .left-icon.likes-icon");
                if (likeElement != null && viewElement != null) {
                    like = likeElement.text().trim();
                    view = viewElement.text().trim();
                }
                if (view.contains("k")) {
                    view = view.substring(0, view.length() - 1);
                    viewD = Double.parseDouble(view) * 1000;
                } else {
                    viewD = Double.parseDouble(view);
                }
                likeD = Double.parseDouble(like);
                rateD = (likeD / viewD * 100);
                String rateStr = String.format(Locale.ENGLISH, "%.2f", rateD);
                System.out.println(rateStr);
                //Show like/view rate END

                Element img = node.getElementsByTag("img").first();
                String imgSrc = img.attr("src");

                Element link = node.getElementsByTag("a").first();
                String linkHref = link.attr("href");
                String address = GetVideoAddress(linkHref, resolution);
                if (!address.contains("file.php")) {
                    //TODO 视频来自外站
                    videoInfo.setAddress("TempAddressForOuterVideo");
                } else {
                    videoInfo.setAddress(address);
                }

                System.out.println(imgSrc);
                videoInfo.setImgSrc(imgSrc);
                videoInfo.setLike(like);
                videoInfo.setView(view);
                videoInfo.setRate(rateStr);
                videoInfo.setPageNum(pageNum);
                videoInfo.setTitle("");
                videoInfos.add(videoInfo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String GetVideoAddress(String url, String resolution) {
        try {
            int resolutionIndex = 0;
            if (resolution != null) {
                if (resolution.contains("540")) {
                    resolutionIndex = 1;
                } else if (resolution.contains("360")) {
                    resolutionIndex = 2;
                }
            }
            url = DOMAIN + "/api" + url.replace("videos", "video");
            Connection.Response res = Jsoup.connect(url)
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                    .timeout(10000).ignoreContentType(true).execute();//.get();
            String body = res.body();
            Type type = new TypeToken<ArrayList<VideoAddressJson>>() {
            }.getType();
            ArrayList<VideoAddressJson> address = gson.fromJson(body, type);
            return address.get(resolutionIndex).getUri();
        } catch (IOException e) {
            e.printStackTrace();
            return "Get Video Address Failed!";
        } catch (IndexOutOfBoundsException e) {
            return "Video not on iwara!";
        }
    }
}
