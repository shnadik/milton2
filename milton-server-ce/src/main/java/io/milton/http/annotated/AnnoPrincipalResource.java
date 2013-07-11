/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.milton.http.annotated;

import io.milton.annotations.Calendars;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.values.HrefList;
import io.milton.http.values.SupportedCalendarComponentListsSet;
import io.milton.principal.CalDavPrincipal;
import io.milton.principal.CardDavPrincipal;
import io.milton.principal.DiscretePrincipal;
import io.milton.principal.HrefPrincipleId;
import io.milton.resource.ICalResource;
import io.milton.resource.Resource;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brad
 */
public class AnnoPrincipalResource extends AnnoCollectionResource implements DiscretePrincipal, CalDavPrincipal, CardDavPrincipal {

	private static final Logger log = LoggerFactory.getLogger(AnnoPrincipalResource.class);

	public AnnoPrincipalResource(AnnotationResourceFactory outer, Object source, AnnoCollectionResource parent) {
		super(outer, source, parent);
	}

	@Override
	public HrefList getCalendarHomeSet() {
		try {
			HrefList list = new HrefList();
			if (annoFactory.calendarsAnnotationHandler.hasCalendars(this.getSource())) {
				list.add(this.getHref());
			}

			for (Resource r : getChildren()) {
				if (r instanceof AnnoCollectionResource) {
					AnnoCollectionResource col = (AnnoCollectionResource) r;
					if (annoFactory.calendarsAnnotationHandler.hasCalendars(col.getSource())) {
						list.add(col.getHref());
					}
				}
			}
			if (list.isEmpty()) {
				ResourceList topDirs = getChildren().getDirs();
				log.warn("Could not find any calendar home directories for user type: " + getSource().getClass() + " You should have a @" + Calendars.class + " annotation for the user object itself, or for a directory within the user home");
				for (Resource r : topDirs) {
					if (r instanceof AnnoCollectionResource) {
						AnnoCollectionResource col = (AnnoCollectionResource) r;
						List<ControllerMethod> candMethods = annoFactory.calendarsAnnotationHandler.getMethods(col.getSource().getClass());
						if (candMethods.isEmpty()) {
							log.info("	- inspecting: " + col.getName() + " for source: " + col.getSource().getClass() + " - has NO child methods");
						} else {
							log.info("	- inspecting: " + col.getName() + " for source: " + col.getSource().getClass());
							for (ControllerMethod cm : candMethods) {
								log.warn("	- candidate method: " + cm.controller.getClass() + "::" + cm.method.getName());
							}
						}
					} else {
						log.warn("	- found a directory which is not a AnnoCollectionResource: " + r.getClass() + " which cannot be inspected");
					}
				}
			}
			return list;
		} catch (NotAuthorizedException e) {
			throw new RuntimeException(e);
		} catch (BadRequestException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public HrefList getAddressBookHomeSet() {
		try {
			HrefList list = new HrefList();
			if (annoFactory.addressBooksAnnotationHandler.hasAddressBooks(this.getSource())) {
				list.add(this.getHref());
			}

			for (Resource r : getChildren()) {
				if (r instanceof AnnoCollectionResource) {
					AnnoCollectionResource col = (AnnoCollectionResource) r;
					if (annoFactory.addressBooksAnnotationHandler.hasAddressBooks(col.getSource())) {
						list.add(col.getHref());
					}
				}
			}
			return list;
		} catch (NotAuthorizedException e) {
			throw new RuntimeException(e);
		} catch (BadRequestException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getPrincipalURL() {
		return getHref();
	}

	@Override
	public PrincipleId getIdenitifer() {
		return new HrefPrincipleId(getHref());
	}

	@Override
	public HrefList getCalendarUserAddressSet() {
		return HrefList.asList(getHref());
	}

	@Override
	public String getDropBoxUrl() {
		return null;
	}

	@Override
	public SupportedCalendarComponentListsSet getSupportedComponentSets() {
		return SupportedCalendarComponentListsSet.EVENTS_ONLY;
	}

	@Override
	public String getAddress() {
		return getHref();
	}
}
