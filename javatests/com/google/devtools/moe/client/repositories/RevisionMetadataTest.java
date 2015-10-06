// Copyright 2011 The MOE Authors All Rights Reserved.

package com.google.devtools.moe.client.repositories;

import com.google.common.collect.ImmutableList;

import junit.framework.TestCase;

import org.joda.time.DateTime;

/**
 * Tests for RevisionMetadata operations.
 *
 */
public class RevisionMetadataTest extends TestCase {

  public void testConcatenate_singleMetadata() {
    RevisionMetadata rm =
        new RevisionMetadata(
            "id",
            "auth",
            new DateTime(1L),
            "description",
            ImmutableList.of(Revision.create("revId", "repo")));

    assertEquals(rm, RevisionMetadata.concatenate(ImmutableList.of(rm), null));
  }

  public void testConcatenate_twoMetadata() {
    RevisionMetadata rm1 =
        new RevisionMetadata(
            "id1",
            "auth1",
            new DateTime(1L),
            "description1",
            ImmutableList.of(Revision.create("revId1", "repo")));
    RevisionMetadata rm2 =
        new RevisionMetadata(
            "id2",
            "auth2",
            new DateTime(2L),
            "description2",
            ImmutableList.of(Revision.create("revId2", "repo")));

    RevisionMetadata rmExpected =
        new RevisionMetadata(
            "id1, id2",
            "auth1, auth2",
            new DateTime(2L),
            "description1\n-------------\ndescription2",
            ImmutableList.of(Revision.create("revId1", "repo"), Revision.create("revId2", "repo")));

    assertEquals(rmExpected, RevisionMetadata.concatenate(ImmutableList.of(rm1, rm2), null));
  }

  public void testConcatenate_withMigrationInfo() {
    RevisionMetadata rm1 =
        new RevisionMetadata(
            "id1",
            "auth1",
            new DateTime(1L),
            "description1",
            ImmutableList.of(Revision.create("revId1", "repo")));
    RevisionMetadata rm2 =
        new RevisionMetadata(
            "id2",
            "auth2",
            new DateTime(2L),
            "description2",
            ImmutableList.of(Revision.create("revId2", "repo")));
    Revision migrationFromRev = Revision.create("migrationRevId", "repo");

    RevisionMetadata rmExpected =
        new RevisionMetadata(
            "id1, id2",
            "auth1, auth2",
            new DateTime(2L),
            "description1\n-------------\ndescription2"
                + "\n-------------\nCreated by MOE: https://github.com/google/moe\n"
                + "MOE_MIGRATED_REVID="
                + migrationFromRev.revId(),
            ImmutableList.of(Revision.create("revId1", "repo"), Revision.create("revId2", "repo")));

    assertEquals(
        rmExpected, RevisionMetadata.concatenate(ImmutableList.of(rm1, rm2), migrationFromRev));
  }
}
