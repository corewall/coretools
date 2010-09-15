/*
 * Copyright (c) Josh Reed, 2009.
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
package org.corewall.scene.event;

import java.util.HashMap;
import java.util.Map;

import org.corewall.scene.Part;

/**
 * A base class for all Scene events.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public abstract class SceneEvent {
	// key/mouse masks
	protected static final int ALT_DOWN_MASK = 1 << 9;
	protected static final int BUTTON1_DOWN_MASK = 1 << 10;
	protected static final int BUTTON2_DOWN_MASK = 1 << 11;
	protected static final int BUTTON3_DOWN_MASK = 1 << 12;
	protected static final int CTRL_DOWN_MASK = 1 << 7;
	protected static final int META_DOWN_MASK = 1 << 8;
	protected static final int SHIFT_DOWN_MASK = 1 << 6;

	protected boolean consumed = false;
	protected int modifiers = 0;
	protected Part target;
	protected Object source;
	protected Map<String, String> properties = null;

	/**
	 * Create a new SceneEvent.
	 */
	public SceneEvent() {
	}

	/**
	 * Mark this event as consumed.
	 */
	public void consume() {
		consumed = true;
	}

	/**
	 * Gets the event modifiers.
	 * 
	 * @return the modifiers.
	 */
	public int getModifiers() {
		return modifiers;
	}

	/**
	 * Gets a property of this event.
	 * 
	 * @param name
	 *            the property.
	 * @return the value of the property.
	 */
	public String getProperty(final String name) {
		return (properties == null) ? null : properties.get(name);
	}

	/**
	 * Gets the source of this event.
	 * 
	 * @return the source.
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * Gets the target of this event.
	 * 
	 * @return the target.
	 */
	public Part getTarget() {
		return target;
	}

	/**
	 * Check whether the alt key was down during the event.
	 * 
	 * @return true if the alt key was down during the event.
	 */
	public boolean isAltDown() {
		return (modifiers & ALT_DOWN_MASK) != 0;
	}

	/**
	 * Checks whether this event has been consumed.
	 * 
	 * @return true if the event is consumed, false otherwise.
	 */
	public boolean isConsumed() {
		return consumed;
	}

	/**
	 * Check whether the ctrl key was down during the event.
	 * 
	 * @return true if the ctrl key was down during the event.
	 */
	public boolean isControlDown() {
		return (modifiers & CTRL_DOWN_MASK) != 0;
	}

	/**
	 * Check whether the meta key was down during the event.
	 * 
	 * @return true if the meta key was down during the event.
	 */
	public boolean isMetaDown() {
		return (modifiers & META_DOWN_MASK) != 0;
	}

	/**
	 * Check whether the shift key was down during the event.
	 * 
	 * @return true if the shift key was down during the event.
	 */
	public boolean isShiftDown() {
		return (modifiers & SHIFT_DOWN_MASK) != 0;
	}

	/**
	 * Sets the event modifiers.
	 * 
	 * @param modifiers
	 *            the modifiers.
	 */
	public void setModifiers(final int modifiers) {
		this.modifiers = modifiers;
	}

	/**
	 * Sets a property of this event.
	 * 
	 * @param name
	 *            the name.
	 * @param value
	 *            the value.
	 */
	public void setProperty(final String name, final String value) {
		if (properties == null) {
			properties = new HashMap<String, String>();
		}
		properties.put(name, value);
	}

	/**
	 * Sets the source of this event.
	 * 
	 * @param source
	 *            the source.
	 */
	public void setSource(final Object source) {
		this.source = source;
	}

	/**
	 * Sets the target of this event.
	 * 
	 * @param target
	 *            the target.
	 */
	public void setTarget(final Part target) {
		this.target = target;
	}
}
