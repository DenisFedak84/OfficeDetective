package criminalintent.android.bignerdranch.com.criminalintent;


import android.annotation.TargetApi;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

//public abstract class SingleFragmentActivity extends FragmentActivity {
public abstract class SingleFragmentActivity  extends AppCompatActivity {
    protected abstract Fragment createFragment();

    protected abstract String lable();

    // возвращает индентификатор макета заполняемого активностью
    // субклассы могут переопределять макет
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }


    @TargetApi(11)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

         //   ActionBar actionBar = getActionBar();
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(lable());
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }
}
