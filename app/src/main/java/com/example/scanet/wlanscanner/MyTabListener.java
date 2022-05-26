/*
 *  Copyright (C) 2014 Benjamin W. (bitbatzen@gmail.com)
 *
 *  This file is part of WLANScanner.
 *
 *  WLANScanner is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  WLANScanner is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with WLANScanner.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.example.scanet.wlanscanner;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;

import com.example.scanet.R;

public class MyTabListener implements ActionBar.TabListener, androidx.appcompat.app.ActionBar.TabListener {
	
	Fragment fragment;
	wifi_scanner wifiscanner;
	
	public MyTabListener(wifi_scanner wifiscanner, Fragment fragment) {
		this.fragment = fragment;
		this.wifiscanner = wifiscanner;
	}
	
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
    	if (fragment instanceof FragmentWLANList) {
			wifiscanner.setCurrentFragmentID(wifi_scanner.FRAGMENT_ID_WLANLIST);
    	}
    	else if (fragment instanceof FragmentDiagram24GHz) {
			wifiscanner.setCurrentFragmentID(wifi_scanner.FRAGMENT_ID_DIAGRAM_24GHZ);
    	}
    	else if (fragment instanceof FragmentDiagram5GHz) {
			wifiscanner.setCurrentFragmentID(wifi_scanner.FRAGMENT_ID_DIAGRAM_5GHZ);
    	}
		else if (fragment instanceof FragmentDiagram6GHz) {
			wifiscanner.setCurrentFragmentID(wifi_scanner.FRAGMENT_ID_DIAGRAM_6GHZ);
		}

		ft.replace(R.id.activity_wifiscanner, fragment);
	}
	
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(fragment);
	}
	
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// nothing done here
	}

    @Override
    public void onTabSelected(androidx.appcompat.app.ActionBar.Tab tab, androidx.fragment.app.FragmentTransaction ft) {}


    @Override
    public void onTabUnselected(androidx.appcompat.app.ActionBar.Tab tab, androidx.fragment.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(androidx.appcompat.app.ActionBar.Tab tab, androidx.fragment.app.FragmentTransaction ft) {

    }
}