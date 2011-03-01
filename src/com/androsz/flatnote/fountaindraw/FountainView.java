/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.androsz.flatnote.fountaindraw;

import android.content.Context;
import android.renderscript.RSSurfaceView;
import android.renderscript.RenderScriptGL;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class FountainView extends RSSurfaceView {

	private RenderScriptGL mRS;

	private FountainRS mRender;

	public FountainView(Context context) {
		super(context);
		// setFocusable(true);
	}

	@Override
	protected void onDetachedFromWindow() {
		if (mRS != null) {
			mRS = null;
			destroyRenderScriptGL();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int act = ev.getActionMasked();
		if (act == MotionEvent.ACTION_UP) {
			mRender.newTouchPosition(0, 0, 0, ev.getPointerId(0));
			return false;
		} else if (act == MotionEvent.ACTION_POINTER_UP) {
			// only one pointer going up, we can get the index like this
			final int pointerIndex = ev.getActionIndex();
			final int pointerId = ev.getPointerId(pointerIndex);
			mRender.newTouchPosition(0, 0, 0, pointerId);
		}
		final int count = ev.getHistorySize();
		final int pcount = ev.getPointerCount();

		for (int p = 0; p < pcount; p++) {
			final int id = ev.getPointerId(p);
			mRender.newTouchPosition(ev.getX(p), ev.getY(p), ev.getPressure(p),
					id);

			for (int i = 0; i < count; i++) {
				mRender.newTouchPosition(ev.getHistoricalX(p, i),
						ev.getHistoricalY(p, i),
						ev.getHistoricalPressure(p, i), id);
			}
		}
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		super.surfaceChanged(holder, format, w, h);
		if (mRS == null) {
			final RenderScriptGL.SurfaceConfig sc = new RenderScriptGL.SurfaceConfig();
			mRS = createRenderScriptGL(sc);
			mRS.setSurface(holder, w, h);
			mRender = new FountainRS();
			mRender.init(mRS, getResources(), w, h);
		}
	}
}
