/*
 *  Copyright (C) 2020 Benjamin W. (bitbatzen@gmail.com)
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

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scanet.R;
import com.example.scanet.wlanscanner.events.EventManager;
import com.example.scanet.wlanscanner.events.Events.EventID;
import com.example.scanet.wlanscanner.events.IEventListener;

public class FragmentDiagram6GHz
		extends Fragment
		implements IEventListener {
	
	LevelDiagram6GHz levelDiagram;
	
    private wifi_scanner wifiscanner;

    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    	wifiscanner = (wifi_scanner) activity;
    	EventManager.sharedInstance().addListener(this, EventID.SCAN_RESULT_CHANGED);
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_wlan_diagram_6ghz, container, false);
		
        levelDiagram = (LevelDiagram6GHz) view.findViewById(R.id.levelDiagram6GHz);
        levelDiagram.updateDiagram(wifiscanner.getScanResults());

        getActivity().invalidateOptionsMenu();
		
		return view;
	}
	
	@Override
	public void handleEvent(EventID eventID) {
		switch (eventID) {
		case SCAN_RESULT_CHANGED:
			levelDiagram.updateDiagram(wifiscanner.getScanResults());
			break;
		default:
			break;
		}
		
	}
}