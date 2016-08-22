/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick.physics;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.widget.ListView;
import android.widget.Spinner;

import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class SetPhysicsObjectTypeBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {

	private static final String TAG = SetPhysicsObjectTypeBrickTest.class.getSimpleName();

	private Solo solo;
	private Project project;
	private SetPhysicsObjectTypeBrick setPhysicsObjectTypeBrick;

	public SetPhysicsObjectTypeBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			Log.e(TAG, "Exception during tearDown", e);
		}

		getActivity().finish();
		super.tearDown();
	}

	@Smoke
	public void testPhysicsObjectTypeBrick() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getDefaultScene().getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		String textSetPhysicsObjectType = solo.getString(R.string.brick_set_physics_object_type);
		assertNotNull("TextView does not exist.", solo.getText(textSetPhysicsObjectType));

		checkSpinnerItemPressed(0);
		checkSpinnerItemPressed(1);
		checkSpinnerItemPressed(2);
	}

	private void checkSpinnerItemPressed(int spinnerItemIndex) {
		String[] physicsObjectTypes = getActivity().getResources().getStringArray(R.array.physics_object_types);

		solo.pressSpinnerItem(0, spinnerItemIndex);
		solo.sleep(200);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		PhysicsObject.Type choosenPhysicsType = (PhysicsObject.Type) Reflection.getPrivateField(
				setPhysicsObjectTypeBrick, "type");
		assertEquals("Wrong text in field.", PhysicsObject.Type.values()[spinnerItemIndex], choosenPhysicsType);
		assertEquals("Value in Brick is not updated.", physicsObjectTypes[spinnerItemIndex],
				solo.getCurrentViews(Spinner.class).get(0).getSelectedItem());
	}

	private void createProject() {
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();
		setPhysicsObjectTypeBrick = new SetPhysicsObjectTypeBrick(PhysicsObject.Type.DYNAMIC);
		script.addBrick(setPhysicsObjectTypeBrick);

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
