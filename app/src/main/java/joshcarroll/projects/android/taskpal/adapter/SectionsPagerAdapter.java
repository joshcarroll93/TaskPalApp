package joshcarroll.projects.android.taskpal.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import joshcarroll.projects.android.taskpal.fragment.TabbedPlaceholderFragment;


public class SectionsPagerAdapter extends FragmentPagerAdapter{

    public TabbedPlaceholderFragment allTasksFragment;
    public TabbedPlaceholderFragment activeTasksFragment;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return TabbedPlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        switch (position){
            case 0 :
                allTasksFragment = (TabbedPlaceholderFragment) createdFragment;
                break;
            case 1 :
                activeTasksFragment = (TabbedPlaceholderFragment) createdFragment;
                break;
        }
        return createdFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "All Tasks";
            case 1:
                return "Active Tasks";
        }
        return null;
    }
}

