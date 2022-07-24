/*
 * Copyright 2018-2020 Florian Spie�
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package club.minnced.discord.webhook.send.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ActionRow implements LayoutComponent {

	private final List<ActionComponent> components;

	private ActionRow(@NotNull List<ActionComponent> components) {
		validate(components);
		this.components = components;
	}

	/**
	 * Creates an action row with the given list of components
	 *
	 * @param  components
	 *         The components to be added to the action row
	 * @throws IllegalStateException
	 *         If a select menu is added with any other component
	 * @throws IllegalStateException
	 *         If more than {@value LayoutComponent#MAX_COMPONENTS} buttons are added in the same action row
	 * @return An action row containing provided components, or null if the collection is empty or null
	 */
	@Nullable
	public ActionRow of(@NotNull Collection<? extends ActionComponent> components) {
		if (components.size() == 0) return null;
		return new ActionRow(new ArrayList<>(components));
	}

	/**
	 * Creates an action row with the given list of components
	 *
	 * @param  components
	 *         The components to be added to the action row
	 * @throws IllegalStateException
	 *         If a select menu is added with any other component
	 * @throws IllegalStateException
	 *         If more than {@value LayoutComponent#MAX_COMPONENTS} buttons are added in the same action row
	 * @return An action row containing provided components, or null if the provided array is empty or null
	 */
	@Nullable
	public static ActionRow of(@NotNull ActionComponent... components) {
		if (components == null || components.length == 0) return null;
		return new ActionRow(Arrays.asList(components));
	}

	@Override
	@NotNull
	public Type getType() {
		return Type.ACTION_ROW;
	}

	@Override
	@NotNull
	public List<ActionComponent> getComponents() {
		return this.components;
	}

	@Override
	@NotNull
	public LayoutComponent addComponent(ActionComponent component) {
		List<ActionComponent> newList = new ArrayList<>(this.components);
		newList.add(component);
		validate(newList);
		this.components.add(component);
		return this;
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		json.put("type", this.getType().getId());
		json.put("components", this.components);
		return json.toString();
	}

	private void validate(List<ActionComponent> components) {
		int buttonCount = 0;
		boolean hasSelectMenu = false;
		for (ActionComponent component : components) {
			if (component instanceof Button) buttonCount++;
			else if (component instanceof SelectMenu) hasSelectMenu = true;
			else throw new IllegalArgumentException("Provided component not an instance of Button or SelectMenu");
		}
		if (hasSelectMenu && components.size() > 1)
			throw new IllegalArgumentException("An action row containing a select menu cant have have more than 1 component");
		if (buttonCount > Button.MAX_BUTTONS)
			throw new IllegalArgumentException("An action row cannot contain more than " + Button.MAX_BUTTONS + " buttons");
	}
}