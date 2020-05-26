package constant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Constant {
    private static final String REST_URL = "https://5025835.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=181&deploy=1";

    public static void openActivity(Context context, Class screen) {
        Intent intent3 = new Intent(context, screen);
        context.startActivity(intent3);
    }

    public static void openActivityWithFinish(Context context, Class screen) {
        Intent intent3 = new Intent(context, screen);
        context.startActivity(intent3);
        ((Activity) context).finish();
    }
    public static void showToast(Context context,String msg) {
        Toast.makeText(context,msg, Toast.LENGTH_LONG).show();
    }
}
