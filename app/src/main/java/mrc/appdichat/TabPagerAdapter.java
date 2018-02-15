package mrc.appdichat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by HP on 03-02-2018.
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {


    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RequestsFragment();
            case 1:
                return new ChatFragment();
            case 2:
                return new FriendsFragment();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Requests";
            case 1:
                return "Chat";
            case 2:
                return "Friends";
            default:
                return null;

        }
    }
}
