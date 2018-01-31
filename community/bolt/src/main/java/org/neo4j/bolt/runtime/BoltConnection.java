/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
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
package org.neo4j.bolt.runtime;

import io.netty.channel.Channel;

import java.net.SocketAddress;

import org.neo4j.bolt.v1.runtime.Job;

public interface BoltConnection
{

    /**
     * Returns a unique, not changing over time string that can safely be used to identify this connection.
     *
     * @return identifier
     */
    String id();

    /**
     * Returns the local (server) socket address that this client is bound to
     *
     * @return local endpoint
     */
    SocketAddress localAddress();

    /**
     * Returns the remote (client) socket address that this client established the connection from.
     *
     * @return remote endpoint
     */
    SocketAddress remoteAddress();

    /**
     * Returns the underlying raw netty channel
     *
     * @return netty channel
     */
    Channel channel();

    /**
     * Returns the principal the client used to connect
     *
     * @return user name
     */
    String principal();

    /**
     * Returns whether this connection has high priority (administrative access) over the standard connections
     *
     * @return true when it is assigned higher priority
     */
    boolean isOutOfBand();

    /**
     * Returns whether there's any pending Job waiting to be processed
     *
     * @return true when there's at least one job in the queue
     */
    boolean hasPendingJobs();

    /**
     * Executes extra initialisation routines before taking this connection into use
     */
    void start();

    /**
     * Adds submitted job to the job queue for execution (at the earliest time possible)
     *
     * @param job the job to be added
     */
    void enqueue( Job job );

    /**
     * Executes a batch of queued jobs, which is executed in an another thread (which is part of a thread pool)
     */
    void processNextBatch();

    /**
     * Interrupt and (possibly) stop the current running job, but continue processing next jobs
     */
    void interrupt();

    /**
     * Stops this connection
     */
    void stop();

}
