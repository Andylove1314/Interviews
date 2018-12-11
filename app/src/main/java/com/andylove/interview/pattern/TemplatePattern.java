package com.andylove.interview.pattern;

/**
 * 模板模式
 */
public class TemplatePattern {

    public static abstract class Activity{
        public abstract void onCreate();
        public abstract void onResume();
        public abstract void onStop();

        public final void startActivity(){
            onCreate();
            onResume();
            onStop();
        }
    }

    /**
     * 修改部分算法的activity
     */
    public static class FragmentActivity extends Activity{
        @Override
        public void onCreate() {
            System.out.print("FragmentActivity onCreate\n");
        }

        @Override
        public void onResume() {
            System.out.print("FragmentActivity onResume\n");

        }

        @Override
        public void onStop() {
            System.out.print("FragmentActivity onStop\n");

        }

    }

    /**
     * 修改部分算法的activity
     */
    public static class FlutterActivity extends Activity{
        @Override
        public void onCreate() {
            System.out.print("FlutterActivity onCreate\n");
        }

        @Override
        public void onResume() {

            System.out.print("FlutterActivity onResume\n");
        }

        @Override
        public void onStop() {

            System.out.print("FlutterActivity onStop\n");
        }

    }

    public static void main(String[] args){
        Activity activity = new FragmentActivity();
        activity.startActivity();
        activity = new FlutterActivity();
        activity.startActivity();
    }

}
