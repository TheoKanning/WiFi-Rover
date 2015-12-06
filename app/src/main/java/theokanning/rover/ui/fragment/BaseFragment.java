package theokanning.rover.ui.fragment;

import android.support.v4.app.Fragment;

import theokanning.rover.ui.activity.BaseActivity;


public abstract class BaseFragment extends Fragment implements FragmentInterface {

    /**
     * Generic newinstance method to work for all fragments
     */
    public static <T extends android.support.v4.app.Fragment> T newInstance(Class<T> fragmentType) {
        try {
            return fragmentType.newInstance();
        } catch (Exception e) {
            //NOTE: Intentionally do nothing
        }
        return null;
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}