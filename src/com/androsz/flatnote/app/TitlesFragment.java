/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.androsz.flatnote.app;

import android.app.ListFragment;
import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androsz.flatnote.R;

public class TitlesFragment extends ListFragment {
	private static class MyDragShadowBuilder extends View.DragShadowBuilder {
		private static Drawable shadow;

		public MyDragShadowBuilder(View v) {
			super(v);
			shadow = new ColorDrawable(Color.BLUE);
			shadow.setBounds(0, 0, v.getWidth(), v.getHeight());
		}

		@Override
		public void onDrawShadow(Canvas canvas) {
			shadow.draw(canvas);
		}
	}

	private int mCategory = 0;

	private int mCurPosition = 0;

	private static String[] DEMO = new String[] { "Red", "Green", "Blue" };

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Current position should survive screen rotations.
		if (savedInstanceState != null) {
			mCategory = savedInstanceState.getInt("category");
			mCurPosition = savedInstanceState.getInt("listPosition");
		}

		populateTitles(mCategory);
		final ListView lv = getListView();
		lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		selectPosition(mCurPosition);
		lv.setCacheColorHint(Color.WHITE);

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos,
					long id) {
				final String title = (String) ((TextView) v).getText();

				// Set up clip data with the category||entry_id format.
				final String textData = String.format("%d||%d", mCategory, pos);
				final ClipData data = ClipData.newPlainText(title, textData);
				v.startDrag(data, new MyDragShadowBuilder(v), null, 0);
				return true;
			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		updateImage(position);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("listPosition", mCurPosition);
		outState.putInt("category", mCategory);
	}

	public void populateTitles(int category) {
		// DirectoryCategory cat = Directory.getCategory(category);
		// String[] items = new String[cat.getEntryCount()];
		// for (int i = 0; i < cat.getEntryCount(); i++)
		// items[i] = cat.getEntry(i).getName();
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				R.layout.title_list_item, DEMO));
		mCategory = category;
	}

	public void selectPosition(int position) {
		final ListView lv = getListView();
		lv.setItemChecked(position, true);
		updateImage(position);

	}

	private void updateImage(int position) {
		final ImageView iv = (ImageView) getFragmentManager()
				.findFragmentById(R.id.frag_content).getView()
				.findViewById(R.id.image);
		if (iv != null) {
			// iv.setImageDrawable(Directory.getCategory(mCategory).getEntry(position)
			// .getDrawable(getResources()));
			int drawableId = 0;
			switch (position) {
			case 0:
				drawableId = R.drawable.red_balloon;
				break;

			case 1:
				drawableId = R.drawable.green_balloon;
				break;

			case 2:
				drawableId = R.drawable.blue_balloon;
				break;

			}
			iv.setImageDrawable(getResources().getDrawable(drawableId));
		}
		mCurPosition = position;
	}
}
