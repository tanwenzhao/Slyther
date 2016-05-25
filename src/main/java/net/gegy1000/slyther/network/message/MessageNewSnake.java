package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.ProfanityHandler;
import net.gegy1000.slyther.game.Skin;
import net.gegy1000.slyther.game.Snake;
import net.gegy1000.slyther.game.SnakePoint;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

import java.util.ArrayList;
import java.util.List;

public class MessageNewSnake extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int id = buffer.readShort();
        if (buffer.hasRemaining(4)) {
            float angle = (float) (buffer.readInt24() * (2 * Math.PI / 0xFFFFFF));
            buffer.skipBytes(1);
            float wang = (float) (buffer.readInt24() * (2 * Math.PI / 0xFFFFFF));
            float sp = buffer.readShort() / 1000.0F;
            double fam = (double) buffer.readInt24() / 0xFFFFFF;
            int ski = buffer.read();
            Skin skin = Skin.values()[ski < Skin.values().length ? ski : 0];
            float x = buffer.readInt24() / 5.0F;
            float y = buffer.readInt24() / 5.0F;
            String name = "";
            int nameLength = buffer.read();
            for (int i = 0; i < nameLength; i++) {
                name += (char) buffer.read();
            }
            float prevPointX;
            float prevPointY;
            float pointX = 0;
            float pointY = 0;
            List<SnakePoint> points = new ArrayList<>();
            while (buffer.hasRemaining(2)) {
                prevPointX = pointX;
                prevPointY = pointY;
                if (points.isEmpty()) {
                    pointX = buffer.readInt24() / 5.0F;
                    pointY = buffer.readInt24() / 5.0F;
                    prevPointX = pointX;
                    prevPointY = pointY;
                } else {
                    pointX += (buffer.read() - 127) / 2.0F;
                    pointY += (buffer.read() - 127) / 2.0F;
                }
                SnakePoint point = new SnakePoint();
                point.posX = pointX;
                point.posY = pointY;
                point.ebx = pointX - prevPointX;
                point.eby = pointY - prevPointY;
                points.add(point);
            }
            Snake snake = new Snake(client, id, x, y, skin, angle, points);
            if (client.player == null) {
                client.viewX = pointX;
                client.viewY = pointY;
                client.player = snake;
                snake.md = false;
                snake.prevMd = false;
                snake.name = client.configuration.nickname;
            } else {
                snake.name = name;
                if (!ProfanityHandler.INSTANCE.isClean(name)) {
                    snake.name = "";
                }
            }
            snake.eang = snake.wang = wang;
            snake.sp = sp;
            snake.spang = sp / client.SPANG_DIV;
            if (snake.spang > 1.0F) {
                snake.spang = 1.0F;
            }
            snake.fam = fam;
            snake.sc = Math.min(6.0F, 1.0F + (snake.sct - 2.0F) / 106.0F);
            snake.scang = (float) (0.13F + 0.87F * Math.pow((7.0F - snake.sc) / 6.0F, 2.0F));
            snake.ssp = client.NSP1 + client.NSP2 * snake.sc;
            snake.fsp = snake.ssp + 0.1F;
            snake.wsep = snake.sc * 6.0F;
            float max = SlytherClient.NSEP / client.gsc;
            if (snake.wsep < max) {
                snake.wsep = max;
            }
            snake.sep = snake.wsep;

            System.out.println("Added snake \"" + snake.name + "\" with skin " + snake.rcv);
            client.snakes.add(snake);

            snake.snl();
        } else {
            boolean dead = buffer.read() == 1;
            Snake snake = client.getSnake(id);
            if (snake != null) {
                snake.id = -1234;
                if (dead) {
                    snake.dead = true;
                    snake.deadAmt = 0;
                    snake.edir = 0;
                } else {
                    client.snakes.remove(snake);
                }
            }
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 's' };
    }
}
