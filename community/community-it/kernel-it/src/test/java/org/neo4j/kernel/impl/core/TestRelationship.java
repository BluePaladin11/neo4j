/*
 * Copyright (c) 2002-2019 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.internal.helpers.collection.Iterables;
import org.neo4j.kernel.impl.AbstractNeo4jTestCase;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.neo4j.graphdb.RelationshipType.withName;
import static org.neo4j.internal.helpers.collection.Iterables.addAll;
import static org.neo4j.kernel.impl.MyRelTypes.TEST;
import static org.neo4j.kernel.impl.MyRelTypes.TEST2;
import static org.neo4j.kernel.impl.MyRelTypes.TEST_TRAVERSAL;

class TestRelationship extends AbstractNeo4jTestCase
{
    private static final String key1 = "key1";
    private static final String key2 = "key2";
    private static final String key3 = "key3";

    @Test
    void testSimple()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel1 = node1.createRelationshipTo( node2, TEST );
        node1.createRelationshipTo( node2, TEST );
        rel1.delete();
        newTransaction();
        assertHasNext( (ResourceIterable<Relationship>) node1.getRelationships() );
        assertHasNext( (ResourceIterable<Relationship>) node2.getRelationships() );
        assertHasNext( (ResourceIterable<Relationship>) node1.getRelationships( TEST ) );
        assertHasNext( (ResourceIterable<Relationship>) node2.getRelationships( TEST ) );
        assertHasNext( (ResourceIterable<Relationship>) node1.getRelationships( TEST, Direction.OUTGOING ) );
        assertHasNext( (ResourceIterable<Relationship>) node2.getRelationships( TEST, Direction.INCOMING ) );
    }

    @Test
    void testSimple2()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        for ( int i = 0; i < 3; i++ )
        {
            node1.createRelationshipTo( node2, TEST );
            node1.createRelationshipTo( node2, TEST_TRAVERSAL );
            node1.createRelationshipTo( node2, TEST2 );
        }
        allGetRelationshipMethods( node1, Direction.OUTGOING );
        allGetRelationshipMethods( node2, Direction.INCOMING );
        newTransaction();
        allGetRelationshipMethods( node1, Direction.OUTGOING );
        allGetRelationshipMethods( node2, Direction.INCOMING );
        deleteFirst( (ResourceIterable<Relationship>) node1.getRelationships( TEST, Direction.OUTGOING ) );
        deleteFirst( (ResourceIterable<Relationship>) node1.getRelationships( TEST_TRAVERSAL, Direction.OUTGOING ) );
        deleteFirst( (ResourceIterable<Relationship>) node1.getRelationships( TEST2, Direction.OUTGOING ) );
        node1.createRelationshipTo( node2, TEST );
        node1.createRelationshipTo( node2, TEST_TRAVERSAL );
        node1.createRelationshipTo( node2, TEST2 );
        allGetRelationshipMethods( node1, Direction.OUTGOING );
        allGetRelationshipMethods( node2, Direction.INCOMING );
        newTransaction();
        allGetRelationshipMethods( node1, Direction.OUTGOING );
        allGetRelationshipMethods( node2, Direction.INCOMING );
        for ( Relationship rel : node1.getRelationships() )
        {
            rel.delete();
        }
        node1.delete();
        node2.delete();
    }

    @Test
    void testSimple3()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        for ( int i = 0; i < 1; i++ )
        {
            node1.createRelationshipTo( node2, TEST );
            node1.createRelationshipTo( node2, TEST_TRAVERSAL );
            node1.createRelationshipTo( node2, TEST2 );
        }
        allGetRelationshipMethods2( node1, Direction.OUTGOING );
        allGetRelationshipMethods2( node2, Direction.INCOMING );
        newTransaction();
        allGetRelationshipMethods2( node1, Direction.OUTGOING );
        allGetRelationshipMethods2( node2, Direction.INCOMING );
        deleteFirst( (ResourceIterable<Relationship>) node1.getRelationships( TEST, Direction.OUTGOING ) );
        deleteFirst( (ResourceIterable<Relationship>) node1.getRelationships( TEST_TRAVERSAL, Direction.OUTGOING ) );
        deleteFirst( (ResourceIterable<Relationship>) node1.getRelationships( TEST2, Direction.OUTGOING ) );
        node1.createRelationshipTo( node2, TEST );
        node1.createRelationshipTo( node2, TEST_TRAVERSAL );
        node1.createRelationshipTo( node2, TEST2 );
        allGetRelationshipMethods2( node1, Direction.OUTGOING );
        allGetRelationshipMethods2( node2, Direction.INCOMING );
        newTransaction();
        allGetRelationshipMethods2( node1, Direction.OUTGOING );
        allGetRelationshipMethods2( node2, Direction.INCOMING );
        for ( Relationship rel : node1.getRelationships() )
        {
            rel.delete();
        }
        node1.delete();
        node2.delete();
    }

    @Test
    void testSimple4()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        for ( int i = 0; i < 2; i++ )
        {
            node1.createRelationshipTo( node2, TEST );
            node1.createRelationshipTo( node2, TEST_TRAVERSAL );
            node1.createRelationshipTo( node2, TEST2 );
        }
        allGetRelationshipMethods3( node1, Direction.OUTGOING );
        allGetRelationshipMethods3( node2, Direction.INCOMING );
        newTransaction();
        allGetRelationshipMethods3( node1, Direction.OUTGOING );
        allGetRelationshipMethods3( node2, Direction.INCOMING );
        deleteFirst( (ResourceIterable<Relationship>) node1.getRelationships( TEST, Direction.OUTGOING ) );
        int count = 0;
        for ( Relationship rel : node1.getRelationships( TEST_TRAVERSAL, Direction.OUTGOING ) )
        {
            if ( count == 1 )
            {
                rel.delete();
            }
            count++;
        }
        deleteFirst( (ResourceIterable<Relationship>) node1.getRelationships( TEST2, Direction.OUTGOING ) );
        node1.createRelationshipTo( node2, TEST );
        node1.createRelationshipTo( node2, TEST_TRAVERSAL );
        node1.createRelationshipTo( node2, TEST2 );
        allGetRelationshipMethods3( node1, Direction.OUTGOING );
        allGetRelationshipMethods3( node2, Direction.INCOMING );
        newTransaction();
        allGetRelationshipMethods3( node1, Direction.OUTGOING );
        allGetRelationshipMethods3( node2, Direction.INCOMING );
        for ( Relationship rel : node1.getRelationships() )
        {
            rel.delete();
        }
        node1.delete();
        node2.delete();
    }

    private void allGetRelationshipMethods( Node node, Direction dir )
    {
        countRelationships( 9, node.getRelationships() );
        countRelationships( 9, node.getRelationships( dir ) );
        countRelationships( 9, node.getRelationships( TEST, TEST2, TEST_TRAVERSAL ) );
        countRelationships( 6, node.getRelationships( TEST, TEST2 ) );
        countRelationships( 6, node.getRelationships( TEST, TEST_TRAVERSAL ) );
        countRelationships( 6, node.getRelationships( TEST2, TEST_TRAVERSAL ) );
        countRelationships( 3, node.getRelationships( TEST ) );
        countRelationships( 3, node.getRelationships( TEST2 ) );
        countRelationships( 3, node.getRelationships( TEST_TRAVERSAL ) );
        countRelationships( 3, node.getRelationships( TEST, dir ) );
        countRelationships( 3, node.getRelationships( TEST2, dir ) );
        countRelationships( 3, node.getRelationships( TEST_TRAVERSAL, dir ) );
    }

    private void allGetRelationshipMethods2( Node node, Direction dir )
    {
        countRelationships( 3, node.getRelationships() );
        countRelationships( 3, node.getRelationships( dir ) );
        countRelationships( 3, node.getRelationships( TEST, TEST2, TEST_TRAVERSAL ) );
        countRelationships( 2, node.getRelationships( TEST, TEST2 ) );
        countRelationships( 2, node.getRelationships( TEST, TEST_TRAVERSAL ) );
        countRelationships( 2, node.getRelationships( TEST2, TEST_TRAVERSAL ) );
        countRelationships( 1, node.getRelationships( TEST ) );
        countRelationships( 1, node.getRelationships( TEST2 ) );
        countRelationships( 1, node.getRelationships( TEST_TRAVERSAL ) );
        countRelationships( 1, node.getRelationships( TEST, dir ) );
        countRelationships( 1, node.getRelationships( TEST2, dir ) );
        countRelationships( 1, node.getRelationships( TEST_TRAVERSAL, dir ) );
    }

    private void allGetRelationshipMethods3( Node node, Direction dir )
    {
        countRelationships( 6, node.getRelationships() );
        countRelationships( 6, node.getRelationships( dir ) );
        countRelationships( 6, node.getRelationships( TEST, TEST2, TEST_TRAVERSAL ) );
        countRelationships( 4, node.getRelationships( TEST, TEST2 ) );
        countRelationships( 4, node.getRelationships( TEST, TEST_TRAVERSAL ) );
        countRelationships( 4, node.getRelationships( TEST2, TEST_TRAVERSAL ) );
        countRelationships( 2, node.getRelationships( TEST ) );
        countRelationships( 2, node.getRelationships( TEST2 ) );
        countRelationships( 2, node.getRelationships( TEST_TRAVERSAL ) );
        countRelationships( 2, node.getRelationships( TEST, dir ) );
        countRelationships( 2, node.getRelationships( TEST2, dir ) );
        countRelationships( 2, node.getRelationships( TEST_TRAVERSAL, dir ) );
    }

    private void countRelationships( int expectedCount, Iterable<Relationship> rels )
    {
        int count = 0;
        for ( Relationship ignored : rels )
        {
            count++;
        }
        assertEquals( expectedCount, count );
    }

    private void deleteFirst( ResourceIterable<Relationship> iterable )
    {
        try ( ResourceIterator<Relationship> iterator = iterable.iterator() )
        {
            iterator.next().delete();
        }
    }

    @Test
    void testRelationshipCreateAndDelete()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship relationship = node1.createRelationshipTo( node2, TEST );
        Relationship[] relArray1 = getRelationshipArray( node1.getRelationships() );
        Relationship[] relArray2 = getRelationshipArray( node2.getRelationships() );
        assertEquals( 1, relArray1.length );
        assertEquals( relationship, relArray1[0] );
        assertEquals( 1, relArray2.length );
        assertEquals( relationship, relArray2[0] );
        relArray1 = getRelationshipArray( node1.getRelationships( TEST ) );
        assertEquals( 1, relArray1.length );
        assertEquals( relationship, relArray1[0] );
        relArray2 = getRelationshipArray( node2.getRelationships( TEST ) );
        assertEquals( 1, relArray2.length );
        assertEquals( relationship, relArray2[0] );
        relArray1 = getRelationshipArray( node1.getRelationships( TEST, Direction.OUTGOING ) );
        assertEquals( 1, relArray1.length );
        relArray2 = getRelationshipArray( node2.getRelationships( TEST, Direction.INCOMING ) );
        assertEquals( 1, relArray2.length );
        relArray1 = getRelationshipArray( node1.getRelationships( TEST, Direction.INCOMING ) );
        assertEquals( 0, relArray1.length );
        relArray2 = getRelationshipArray( node2.getRelationships( TEST, Direction.OUTGOING ) );
        assertEquals( 0, relArray2.length );
        relationship.delete();
        node2.delete();
        node1.delete();
    }

    private Relationship[] getRelationshipArray( Iterable<Relationship> relsIterable )
    {
        ArrayList<Relationship> relList = new ArrayList<>();
        for ( Relationship rel : relsIterable )
        {
            relList.add( rel );
        }
        return relList.toArray( new Relationship[relList.size()] );
    }

    @Test
    void testDeleteWithRelationship()
    {
        // do some evil stuff
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        node1.createRelationshipTo( node2, TEST );
        node1.delete();
        node2.delete();
        assertThrows( Exception.class, () ->
        {
            getTransaction().success();
            getTransaction().close();
        } );
        setTransaction( getGraphDb().beginTx() );
    }

    @Test
    void testDeletedRelationship()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship relationship = node1.createRelationshipTo( node2, TEST );
        relationship.delete();
        assertThrows( Exception.class, () -> relationship.setProperty( "key1", 1 ) );
        node1.delete();
        node2.delete();
    }

    @Test
    void testRelationshipAddPropertyWithNullKey()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel1 = node1.createRelationshipTo( node2, TEST );

        assertThrows( Exception.class, () -> rel1.setProperty( null, "bar" ) );
    }

    @Test
    void testRelationshipAddPropertyWithNullValue()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel1 = node1.createRelationshipTo( node2, TEST );

        assertThrows( Exception.class, () -> rel1.setProperty( "foo", null ) );

        getTransaction().failure();
    }

    @Test
    void testRelationshipAddProperty()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel1 = node1.createRelationshipTo( node2, TEST );
        Relationship rel2 = node2.createRelationshipTo( node1, TEST );

        Integer int1 = 1;
        Integer int2 = 2;
        String string1 = "1";
        String string2 = "2";

        // add property
        rel1.setProperty( key1, int1 );
        rel2.setProperty( key1, string1 );
        rel1.setProperty( key2, string2 );
        rel2.setProperty( key2, int2 );
        assertTrue( rel1.hasProperty( key1 ) );
        assertTrue( rel2.hasProperty( key1 ) );
        assertTrue( rel1.hasProperty( key2 ) );
        assertTrue( rel2.hasProperty( key2 ) );
        assertFalse( rel1.hasProperty( key3 ) );
        assertFalse( rel2.hasProperty( key3 ) );
        assertEquals( int1, rel1.getProperty( key1 ) );
        assertEquals( string1, rel2.getProperty( key1 ) );
        assertEquals( string2, rel1.getProperty( key2 ) );
        assertEquals( int2, rel2.getProperty( key2 ) );

        getTransaction().failure();
    }

    @Test
    void testRelationshipRemoveProperty()
    {
        Integer int1 = 1;
        Integer int2 = 2;
        String string1 = "1";
        String string2 = "2";

        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel1 = node1.createRelationshipTo( node2, TEST );
        Relationship rel2 = node2.createRelationshipTo( node1, TEST );
        // verify that we can rely on PL to remove non existing properties
        if ( rel1.removeProperty( key1 ) != null )
        {
            fail( "Remove of non existing property should return null" );
        }

        assertThrows( IllegalArgumentException.class, () -> rel1.removeProperty( null ) );

        rel1.setProperty( key1, int1 );
        rel2.setProperty( key1, string1 );
        rel1.setProperty( key2, string2 );
        rel2.setProperty( key2, int2 );
        assertThrows( IllegalArgumentException.class, () -> rel1.removeProperty( null ) );

        // test remove property
        assertEquals( int1, rel1.removeProperty( key1 ) );
        assertEquals( string1, rel2.removeProperty( key1 ) );
        // test remove of non existing property
        if ( rel2.removeProperty( key1 ) != null )
        {
            fail( "Remove of non existing property should return null" );
        }

        rel1.delete();
        rel2.delete();
        node1.delete();
        node2.delete();
    }

    @Test
    void testRelationshipChangeProperty()
    {
        Integer int1 = 1;
        Integer int2 = 2;
        String string1 = "1";
        String string2 = "2";

        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel1 = node1.createRelationshipTo( node2, TEST );
        Relationship rel2 = node2.createRelationshipTo( node1, TEST );
        rel1.setProperty( key1, int1 );
        rel2.setProperty( key1, string1 );
        rel1.setProperty( key2, string2 );
        rel2.setProperty( key2, int2 );

        assertThrows( IllegalArgumentException.class, () -> rel1.setProperty( null, null ) );

        // test type change of existing property
        // cannot test this for now because of exceptions in PL
        rel2.setProperty( key1, int1 );

        rel1.delete();
        rel2.delete();
        node2.delete();
        node1.delete();
    }

    @Test
    void testRelationshipChangeProperty2()
    {
        Integer int1 = 1;
        Integer int2 = 2;
        String string1 = "1";
        String string2 = "2";

        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel1 = node1.createRelationshipTo( node2, TEST );
        rel1.setProperty( key1, int1 );
        rel1.setProperty( key1, int2 );
        assertEquals( int2, rel1.getProperty( key1 ) );
        rel1.removeProperty( key1 );
        rel1.setProperty( key1, string1 );
        rel1.setProperty( key1, string2 );
        assertEquals( string2, rel1.getProperty( key1 ) );
        rel1.removeProperty( key1 );
        rel1.setProperty( key1, true );
        rel1.setProperty( key1, false );
        assertEquals( false, rel1.getProperty( key1 ) );
        rel1.removeProperty( key1 );

        rel1.delete();
        node2.delete();
        node1.delete();
    }

    @Test
    void testRelGetProperties()
    {
        Integer int1 = 1;
        Integer int2 = 2;
        String string = "3";

        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel1 = node1.createRelationshipTo( node2, TEST );
        assertThrows( NotFoundException.class, () -> rel1.getProperty( key1 ) );
        assertThrows( IllegalArgumentException.class, () -> rel1.getProperty( null ) );

        assertFalse( rel1.hasProperty( key1 ) );
        assertFalse( rel1.hasProperty( null ) );
        rel1.setProperty( key1, int1 );
        rel1.setProperty( key2, int2 );
        rel1.setProperty( key3, string );
        assertTrue( rel1.hasProperty( key1 ) );
        assertTrue( rel1.hasProperty( key2 ) );
        assertTrue( rel1.hasProperty( key3 ) );

        Map<String,Object> properties = rel1.getAllProperties();
        assertEquals( properties.get( key1 ), int1 );
        assertEquals( properties.get( key2 ), int2 );
        assertEquals( properties.get( key3 ), string );
        properties = rel1.getProperties( key1, key2 );
        assertEquals( properties.get( key1 ), int1 );
        assertEquals( properties.get( key2 ), int2 );
        assertFalse( properties.containsKey( key3 ) );

        properties = node1.getProperties();
        assertTrue( properties.isEmpty() );

        assertThrows( NullPointerException.class, () -> node1.getProperties( (String[]) null ) );

        assertThrows( NullPointerException.class, () ->
        {
            String[] names = new String[]{null};
            node1.getProperties( names );
            fail();
        } );

        assertDoesNotThrow( () -> rel1.removeProperty( key3 ), "Remove of property failed." );

        assertFalse( rel1.hasProperty( key3 ) );
        assertFalse( rel1.hasProperty( null ) );
        rel1.delete();
        node2.delete();
        node1.delete();
    }

    @Test
    void testDirectedRelationship()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel2 = node1.createRelationshipTo( node2, TEST );
        Relationship rel3 = node2.createRelationshipTo( node1, TEST );
        Node[] nodes = rel2.getNodes();
        assertEquals( 2, nodes.length );
        assertTrue( nodes[0].equals( node1 ) && nodes[1].equals( node2 ) );
        nodes = rel3.getNodes();
        assertEquals( 2, nodes.length );
        assertTrue( nodes[0].equals( node2 ) && nodes[1].equals( node1 ) );
        assertEquals( node1, rel2.getStartNode() );
        assertEquals( node2, rel2.getEndNode() );
        assertEquals( node2, rel3.getStartNode() );
        assertEquals( node1, rel3.getEndNode() );

        Relationship[] relArray = getRelationshipArray( node1.getRelationships( TEST, Direction.OUTGOING ) );
        assertEquals( 1, relArray.length );
        assertEquals( rel2, relArray[0] );
        relArray = getRelationshipArray( node1.getRelationships( TEST, Direction.INCOMING ) );
        assertEquals( 1, relArray.length );
        assertEquals( rel3, relArray[0] );

        relArray = getRelationshipArray( node2.getRelationships( TEST, Direction.OUTGOING ) );
        assertEquals( 1, relArray.length );
        assertEquals( rel3, relArray[0] );
        relArray = getRelationshipArray( node2.getRelationships( TEST, Direction.INCOMING ) );
        assertEquals( 1, relArray.length );
        assertEquals( rel2, relArray[0] );

        rel2.delete();
        rel3.delete();
        node1.delete();
        node2.delete();
    }

    @Test
    void testRollbackDeleteRelationship()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel1 = node1.createRelationshipTo( node2, TEST );
        newTransaction();
        node1.delete();
        rel1.delete();
        getTransaction().failure();
        getTransaction().close();
        setTransaction( getGraphDb().beginTx() );
        node1.delete();
        node2.delete();
        rel1.delete();
    }

    @Test
    void testCreateRelationshipWithCommits()
    {
        Node n1 = getGraphDb().createNode();
        newTransaction();
        n1 = getGraphDb().getNodeById( n1.getId() );
        Node n2 = getGraphDb().createNode();
        n1.createRelationshipTo( n2, TEST );
        newTransaction();
        Relationship[] relArray = getRelationshipArray( n1.getRelationships() );
        assertEquals( 1, relArray.length );
        relArray = getRelationshipArray( n1.getRelationships() );
        relArray[0].delete();
        n1.delete();
        n2.delete();
    }

    @Test
    void testAddPropertyThenDelete()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel = node1.createRelationshipTo( node2, TEST );
        rel.setProperty( "test", "test" );
        newTransaction();
        rel.setProperty( "test2", "test2" );
        rel.delete();
        node1.delete();
        node2.delete();
        newTransaction();
    }

    @Test
    void testRelationshipIsType()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel = node1.createRelationshipTo( node2, TEST );
        assertTrue( rel.isType( TEST ) );
        assertTrue( rel.isType( TEST::name ) );
        assertFalse( rel.isType( TEST_TRAVERSAL ) );
        rel.delete();
        node1.delete();
        node2.delete();
    }

    @Test
    void testChangeProperty()
    {
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel = node1.createRelationshipTo( node2, TEST );
        rel.setProperty( "test", "test1" );
        newTransaction();
        rel.setProperty( "test", "test2" );
        rel.removeProperty( "test" );
        rel.setProperty( "test", "test3" );
        assertEquals( "test3", rel.getProperty( "test" ) );
        rel.removeProperty( "test" );
        rel.setProperty( "test", "test4" );
        newTransaction();
        assertEquals( "test4", rel.getProperty( "test" ) );
    }

    @Test
    void testChangeProperty2()
    {
        // Create relationship with "test"="test1"
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        Relationship rel = node1.createRelationshipTo( node2, TEST );
        rel.setProperty( "test", "test1" );
        newTransaction(); // commit

        // Remove "test" and set "test"="test3" instead
        rel.removeProperty( "test" );
        rel.setProperty( "test", "test3" );
        assertEquals( "test3", rel.getProperty( "test" ) );
        newTransaction(); // commit

        // Remove "test" and set "test"="test4" instead
        assertEquals( "test3", rel.getProperty( "test" ) );
        rel.removeProperty( "test" );
        rel.setProperty( "test", "test4" );
        newTransaction(); // commit

        // Should still be "test4"
        assertEquals( "test4", rel.getProperty( "test" ) );
    }

    @Test
    void makeSureLazyLoadingRelationshipsWorksEvenIfOtherIteratorAlsoLoadsInTheSameIteration()
    {
        int numEdges = 100;

        /* create 256 nodes */
        GraphDatabaseService graphDB = getGraphDb();
        Node[] nodes = new Node[256];
        for ( int numNodes = 0; numNodes < nodes.length; numNodes += 1 )
        {
            nodes[numNodes] = graphDB.createNode();
        }
        newTransaction();

        /* create random outgoing relationships from node 5 */
        Node hub = nodes[4];
        int nextID = 7;

        RelationshipType outtie = withName( "outtie" );
        RelationshipType innie = withName( "innie" );
        for ( int k = 0; k < numEdges; k++ )
        {
            Node neighbor = nodes[nextID];
            nextID += 7;
            nextID &= 255;
            if ( nextID == 0 )
            {
                nextID = 1;
            }
            hub.createRelationshipTo( neighbor, outtie );
        }
        newTransaction();

        /* create random incoming relationships to node 5 */
        for ( int k = 0; k < numEdges; k += 1 )
        {
            Node neighbor = nodes[nextID];
            nextID += 7;
            nextID &= 255;
            if ( nextID == 0 )
            {
                nextID = 1;
            }
            neighbor.createRelationshipTo( hub, innie );
        }
        commit();

        newTransaction();
        hub = graphDB.getNodeById( hub.getId() );

        int count = 0;
        for ( Relationship ignore : hub.getRelationships() )
        {
            count += Iterables.count( hub.getRelationships() );
        }
        assertEquals( 40000, count );

        count = 0;
        for ( @SuppressWarnings( "unused" ) Relationship r1 : hub.getRelationships() )
        {
            count += Iterables.count( hub.getRelationships() );
        }
        assertEquals( 40000, count );
        commit();
    }

    @Test
    void createRelationshipAfterClearedCache()
    {
        // Assumes relationship grab size 100
        Node node1 = getGraphDb().createNode();
        Node node2 = getGraphDb().createNode();
        int expectedCount = 0;
        for ( int i = 0; i < 150; i++ )
        {
            node1.createRelationshipTo( node2, TEST );
            expectedCount++;
        }
        newTransaction();
        for ( int i = 0; i < 50; i++ )
        {
            node1.createRelationshipTo( node2, TEST );
            expectedCount++;
        }
        assertEquals( expectedCount, Iterables.count( node1.getRelationships() ) );
        newTransaction();
        assertEquals( expectedCount, Iterables.count( node1.getRelationships() ) );
    }

    @Test
    void getAllRelationships()
    {
        Set<Relationship> existingRelationships = addAll( new HashSet<>(), getGraphDb().getAllRelationships() );

        Set<Relationship> createdRelationships = new HashSet<>();
        Node node = getGraphDb().createNode();
        for ( int i = 0; i < 100; i++ )
        {
            createdRelationships.add( node.createRelationshipTo( getGraphDb().createNode(), TEST ) );
        }
        newTransaction();

        Set<Relationship> allRelationships = new HashSet<>();
        allRelationships.addAll( existingRelationships );
        allRelationships.addAll( createdRelationships );

        int count = 0;
        for ( Relationship rel : getGraphDb().getAllRelationships() )
        {
            assertTrue(
                allRelationships.contains( rel ), "Unexpected rel " + rel + ", expected one of " + allRelationships );
            count++;
        }
        assertEquals( allRelationships.size(), count );
    }

    @Test
    void createAndClearCacheBeforeCommit()
    {
        Node node = getGraphDb().createNode();
        node.createRelationshipTo( getGraphDb().createNode(), TEST );
        assertEquals( 1, Iterables.count( node.getRelationships() ) );
    }

    @Test
    void setPropertyAndClearCacheBeforeCommit()
    {
        Node node = getGraphDb().createNode();
        node.setProperty( "name", "Test" );
        assertEquals( "Test", node.getProperty( "name" ) );
    }

    @Test
    void shouldNotGetTheSameRelationshipMoreThanOnceWhenAskingForTheSameTypeMultipleTimes()
    {
        // given
        Node node = getGraphDb().createNode();
        node.createRelationshipTo( getGraphDb().createNode(), withName( "FOO" ) );

        // when
        long relationships = Iterables.count( node.getRelationships( withName( "FOO" ), withName( "FOO" ) ) );

        // then
        assertEquals( 1, relationships );
    }

    @Test
    void shouldLoadAllRelationships()
    {
        // GIVEN
        GraphDatabaseService db = getGraphDbAPI();
        Node node;
        try ( Transaction tx = db.beginTx() )
        {
            node = db.createNode();
            for ( int i = 0; i < 112; i++ )
            {
                node.createRelationshipTo( db.createNode(), TEST );
                db.createNode().createRelationshipTo( node, TEST );
            }
            tx.success();
        }
        // WHEN
        long one;
        long two;
        try ( Transaction tx = db.beginTx() )
        {
            one = Iterables.count( node.getRelationships( TEST, Direction.OUTGOING ) );
            two = Iterables.count( node.getRelationships( TEST, Direction.OUTGOING ) );
            tx.success();
        }

        // THEN
        assertEquals( two, one );
    }

    @Test
    void deletionOfSameRelationshipTwiceInOneTransactionShouldNotRollbackIt()
    {
        // Given
        GraphDatabaseService db = getGraphDb();

        // transaction is opened by test
        Node node1 = db.createNode();
        Node node2 = db.createNode();
        Relationship relationship = node1.createRelationshipTo( node2, TEST );
        commit();

        try ( Transaction tx = db.beginTx() )
        {
            relationship.delete();
            assertThrows( NotFoundException.class, () -> relationship.delete() );
            tx.success();
        }

        try ( Transaction tx = db.beginTx() )
        {
            assertThrows( NotFoundException.class, () -> db.getRelationshipById( relationship.getId() ) );
            tx.success();
        }
    }

    @Test
    void deletionOfAlreadyDeletedRelationshipShouldThrow()
    {
        // Given
        GraphDatabaseService db = getGraphDb();

        // transaction is opened by test
        Node node1 = db.createNode();
        Node node2 = db.createNode();
        Relationship relationship = node1.createRelationshipTo( node2, TEST );
        commit();

        try ( Transaction tx = db.beginTx() )
        {
            relationship.delete();
            tx.success();
        }

        // When
        try ( Transaction tx = db.beginTx() )
        {
            assertThrows( NotFoundException.class, () -> relationship.delete() );
            tx.success();
        }
    }

    private void assertHasNext( ResourceIterable<Relationship> relationships )
    {
        try ( ResourceIterator<Relationship> iterator = relationships.iterator() )
        {
            assertTrue( iterator.hasNext() );
        }
    }
}