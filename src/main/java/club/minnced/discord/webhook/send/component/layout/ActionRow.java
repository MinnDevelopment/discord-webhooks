/*
 * Copyright 2018-2020 Florian Spieß
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

package club.minnced.discord.webhook.send.component.layout;

import club.minnced.discord.webhook.send.component.ComponentElement;
import club.minnced.discord.webhook.send.component.ComponentLayout;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A row layout for components.
 */
public class ActionRow implements ComponentLayout {
	private final List<ComponentElement> components;

	private ActionRow(@NotNull List<ComponentElement> components) {
		this.components = components;
	}

	/**
	 * Creates an action row with the given list of components
	 *
	 * @param  components
	 *         The components to be added to the action row
	 *
	 * @throws java.lang.NullPointerException
	 * 	       If null is provided
	 *
	 * @return An action row containing provided components, or null if the collection is empty or null
	 */
	@NotNull
	public ActionRow of(@NotNull Collection<? extends ComponentElement> components) {
		Objects.requireNonNull(components).forEach(Objects::requireNonNull);
		return new ActionRow(new ArrayList<>(components));
	}

	/**
	 * Creates an action row with the given list of components
	 *
	 * @param  components
	 *         The components to be added to the action row
	 *
	 * @throws java.lang.NullPointerException
	 * 	       If null is provided
	 *
	 * @return An action row containing provided components, or null if the provided array is empty or null
	 */
	@NotNull
	public static ActionRow of(@NotNull ComponentElement... components) {
		return new ActionRow(Arrays.asList(components));
	}

	@Override
	@NotNull
	public Type getType() {
		return Type.ACTION_ROW;
	}

	@Override
	@NotNull
	public List<ComponentElement> getComponents() {
		return this.components;
	}

	@Override
	@NotNull
	public ComponentLayout addComponents(@NotNull Collection<? extends ComponentElement> components) {
		Objects.requireNonNull(components)
			   .forEach(Objects::requireNonNull);
		this.components.addAll(components);
		return this;
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		json.put("type", this.getType().getId());
		json.put("components", this.components);
		return json.toString();
	}

	@Override
	public boolean isValid() {
		// If there are no components, it is an invalid layout
		if (components.isEmpty())
			return false;

		// Each component has to be valid
		for (ComponentElement component : components) {
			if (!component.isValid())
				return false;
		}

		Map<Type, List<ComponentElement>> types = components.stream().collect(Collectors.groupingBy(ComponentElement::getType));

		// Currently not possible to mix component types in a single layout.
		if (types.size() > 1)
			return false;

		// Each component type has a different number of max components
		for (Map.Entry<Type, List<ComponentElement>> entry : types.entrySet()) {
			if (entry.getValue().size() > entry.getKey().getMaxPerLayout())
				return false;
		}

		return true;
	}
}