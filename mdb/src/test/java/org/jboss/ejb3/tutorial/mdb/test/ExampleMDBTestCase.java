/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ejb3.tutorial.mdb.test;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.tutorial.mdb.bean.ExampleMDB;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
@RunWith(Arquillian.class)
public class ExampleMDBTestCase {
    @Deployment
    public static JavaArchive deployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(ExampleMDB.class)
                .addManifestResource("tutorial-hornetq-jms.xml");
    }

    @Test
    public void testExampleMDB() throws Exception {
        InitialContext ctx = new InitialContext();
        Queue queue = (Queue) ctx.lookup("queue/tutorial/example");
        QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
        QueueConnection cnn = factory.createQueueConnection();
        try {
            QueueSession session = cnn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);

            TextMessage msg = session.createTextMessage("Hello World");

            QueueSender sender = session.createSender(queue);
            sender.send(msg);
            System.out.println("Message sent successfully to remote queue");
        } finally {
            cnn.close();
        }
    }
}
