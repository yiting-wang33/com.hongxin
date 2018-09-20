package testTarget;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import static helper.match.searchDB;
import static testTarget.web_api.getTargetData;
import static testTarget.web_api.getTargetEigen;

/**
 * @author yiting wang
 * @date 7/31/18 10:55
 * @email wang.yitin@husky.neu.edu
 */
public class init {

    public static void main(String[] arg) {
        //Scanner sc = new Scanner(System.in);
        //String photoId = sc.nextLine();
        try {
            for (int i = 61;i<=100;i++) {
                System.out.println("图片："+i);
                web_api.ResultType rt = getTargetEigen(getTargetData(i+"_change"));
                searchDB(rt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
