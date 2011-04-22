package com.androsz.flatnote.app;

import android.os.Bundle;

import com.androsz.flatnote.R;

public class HomeActivity extends HostActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notebooks);
	}
}
