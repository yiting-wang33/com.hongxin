package testTarget;

/**
 * @author yiting wang
 * @date 7/31/18 09:40
 * @email wang.yitin@husky.neu.edu
 */
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;




public class web_api {

    /**
     * 新建一个类，用来存放筛选时需要用到的数据
     * tarEigen 特征值
     * model 车型
     * color 颜色
     */
    public static class ResultType {
        public String tarEigen;
        public String model;
        public String color;
        public String license;
        public ResultType(String tarEigen,String license,String model,String color) {
            this.tarEigen = tarEigen;
            this.license = license;
            this.model = model;
            this.color = color;
        }
    }
    /**
     * 发送HttpPost请求
     *
     * @param strURL
     *            服务地址
     * @param params
     *
     * @return 成功:返回json字符串<br/>
     */
    public static String sendPost(String strURL, String senddata) {
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
            out.append(senddata);
            out.flush();
            out.close();

            int code = connection.getResponseCode();
            InputStream is = null;
            if (code == 200) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }

            // 读取响应
            int length = (int) connection.getContentLength();// 获取长度
            if (length != -1) {
                byte[] data = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) > 0) {
                    System.arraycopy(temp, 0, data, destPos, readLen);
                    destPos += readLen;
                }
                String result = new String(data, "UTF-8"); // utf-8编码
                return result;
            }

        } catch (IOException e) {
            System.out.println("Exception occur when send http post request!");
        }
        return "error"; // 自定义错误信息
    }

    /**
     *
     * @param targetId 被搜图片的Id
     * @return  Api返回的被搜图片的Json格式数据
     * @throws IOException
     */
    public static String getTargetData(String targetId) throws IOException
    {
        Base64Test base64_ = new Base64Test();
        String image_path1 ="/Users/yitinwang/Desktop/鸿信实习2018.07/photo_change/";
        //被搜图片的路径
        image_path1 = image_path1 + targetId +".jpg";
        String b64_1 = base64_.GetImageStr(image_path1);

        Map <String, String> params = new HashMap <String, String>();
        params.put("base64_str", b64_1);
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        params.put("img_id", uuid);


        //String url ="http://127.0.0.1:5000/image_feature_api";
        String url ="http://202.102.101.217:58106/image_vehicle_feature_api";
        //String url = "http://202.102.101.217:58106/image_vehicle_feature_index";
        JSONObject jsonObject = new JSONObject(params);

        String targetData =sendPost( url,  jsonObject.toString());
        System.out.println(targetData);
        targetData = targetData.substring(9,targetData.length()-1);
        return targetData;
    }

    /**
     *
     * @param targetData Json格式
     * @return tarEigen 被搜图片的特征值 "1000_特征值1_特征值2_特征值3_……_特征值1000"
     */
    public static ResultType getTargetEigen(String targetData) {
        JSONObject JsontarData = new JSONObject(targetData);
        String tarData = JsontarData.getString("result");
        //System.out.println("被搜车辆信息："+tarData);
        String[] tarSubStr = tarData.split("\\|");
        ResultType rt = new ResultType(tarSubStr[0],tarSubStr[4],tarSubStr[5],tarSubStr[6]);
        return rt;
    }

}
