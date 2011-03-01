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

import android.content.res.Resources;
import android.renderscript.Mesh;
import android.renderscript.ProgramFragmentFixedFunction;
import android.renderscript.RenderScriptGL;

import com.android.fountain.ScriptC_fountain;
import com.android.fountain.ScriptField_Point;
import com.androsz.flatnote.R;

public class FountainRS {
	public static final int PART_COUNT = 50000;

	private Resources mRes;

	private RenderScriptGL mRS;
	private ScriptC_fountain mScript;
	boolean holdingColor[] = new boolean[10];

	public FountainRS() {
	}

	public void init(RenderScriptGL rs, Resources res, int width, int height) {
		mRS = rs;
		mRes = res;

		final ProgramFragmentFixedFunction.Builder pfb = new ProgramFragmentFixedFunction.Builder(
				rs);
		pfb.setVaryingColor(true);
		rs.bindProgramFragment(pfb.create());

		final ScriptField_Point points = new ScriptField_Point(mRS, PART_COUNT);//
		// Allocation.USAGE_GRAPHICS_VERTEX);

		final Mesh.AllocationBuilder smb = new Mesh.AllocationBuilder(mRS);
		smb.addVertexAllocation(points.getAllocation());
		smb.addIndexSetType(Mesh.Primitive.POINT);
		final Mesh sm = smb.create();

		mScript = new ScriptC_fountain(mRS, mRes, R.raw.fountain);
		mScript.set_partMesh(sm);
		mScript.bind_point(points);
		mRS.bindRootScript(mScript);
	}

	public void newTouchPosition(float x, float y, float pressure, int id) {
		if (id >= holdingColor.length)
			return;
		final int rate = (int) (pressure * pressure * 500.f);
		// if (rate > 150) {
		// rate = 150;
		// }
		if (rate > 0) {
			mScript.invoke_addParticles(rate, x, y, id, !holdingColor[id]);
			holdingColor[id] = true;
		} else {
			holdingColor[id] = false;
		}

	}
}
