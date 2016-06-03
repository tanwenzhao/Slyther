package net.gegy1000.slyther.client.gui;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.entity.ClientSnake;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.LeaderboardEntry;
import net.gegy1000.slyther.game.SkinColor;
import net.gegy1000.slyther.game.entity.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiGame extends Gui {
    @Override
    public void init() {
    }

    @Override
    public void render(float mouseX, float mouseY) {
        boolean loading = client.networkManager == null || client.player == null;
        GL11.glPushMatrix();
        textureManager.bindTexture("/textures/background.png");
        float gsc = client.gsc + client.zoomOffset;
        GL11.glScalef(gsc, gsc, 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef(client.mww2 / gsc, client.mhh2 / gsc, 0.0F);
        ClientSnake player = client.player;
        float delta = client.delta;
        if (!loading) {
            client.viewX = player.getRenderX(delta) + player.fx + client.fvx;
            client.viewY = player.getRenderY(delta) + player.fy + client.fvy;
        }
        client.viewAngle = (float) Math.atan2(client.viewY - client.GAME_RADIUS, client.viewX - client.GAME_RADIUS);
        client.viewDist = (float) Math.sqrt((client.viewX - client.GAME_RADIUS) * (client.viewX - client.GAME_RADIUS) + (client.viewY - client.GAME_RADIUS) * (client.viewY - client.GAME_RADIUS));
        client.bpx1 = client.viewX - (client.mww2 / gsc - 84);
        client.bpy1 = client.viewY - (client.mhh2 / gsc - 84);
        client.bpx2 = client.viewX + (client.mww2 / gsc - 84);
        client.bpy2 = client.viewY + (client.mhh2 / gsc - 84);
        client.fpx1 = client.viewX - (client.mww2 / gsc - 24);
        client.fpy1 = client.viewY - (client.mhh2 / gsc - 24);
        client.fpx2 = client.viewX + (client.mww2 / gsc - 24);
        client.fpy2 = client.viewY + (client.mhh2 / gsc - 24);
        client.apx1 = client.viewX - (client.mww2 / gsc - 210);
        client.apy1 = client.viewY - (client.mhh2 / gsc - 210);
        client.apx2 = client.viewX + (client.mww2 / gsc - 210);
        client.apy2 = client.viewY + (client.mhh2 / gsc - 210);
        float sectionWidth = renderResolution.getWidth() / gsc / 2.0F;
        float sectionHeight = renderResolution.getHeight() / gsc / 2.0F;
        for (int x = -1; x < 1; x++) {
            for (int y = -1; y < 1; y++) {
                float offsetX = x * sectionWidth;
                float offsetY = y * sectionHeight;
                drawTexture(offsetX, offsetY, (loading ? client.renderTicks : client.viewX) + offsetX, client.viewY + offsetY, sectionWidth, sectionHeight, 599, 519);
            }
        }
        GL11.glTranslatef(-client.viewX, -client.viewY, 0.0F);
        if (!loading) {
            float newScale = 0.4F / Math.max(1.0F, (player.sct + 16.0F) / 36.0F) + 0.5F;
            if (client.gsc != newScale) {
                if (client.gsc < newScale) {
                    client.gsc += 0.0001F;
                    if (client.gsc > newScale) {
                        client.gsc = newScale;
                    }
                } else if (client.gsc > newScale) {
                    client.gsc -= 0.0001F;
                    if (client.gsc < newScale) {
                        client.gsc = newScale;
                    }
                }
            }
            if (client.fvtg > 0) {
                client.fvtg--;
                client.fvx = client.fvxs[client.fvpos];
                client.fvy = client.fvys[client.fvpos];
                client.fvxs[client.fvpos] = 0;
                client.fvys[client.fvpos] = 0;
                if (client.fvpos > SlytherClient.VFC) {
                    client.fvpos = 0;
                }
            }
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
            if (client.configuration.debugMode) {
                GL11.glColor4f(0, 1, 0, 0.1F);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                for (Sector sector : client.getSectors()) {
                    GL11.glRectf(
                            sector.posX * client.SECTOR_SIZE,
                            sector.posY * client.SECTOR_SIZE,
                            sector.posX * client.SECTOR_SIZE + client.SECTOR_SIZE - 4,
                            sector.posY * client.SECTOR_SIZE + client.SECTOR_SIZE - 4
                    );
                }
            }
            textureManager.bindTexture("/textures/food.png");
            float globalAlpha = 1.75F;
            if (client.gla != 1.0F) {
                globalAlpha = 1.75F * client.gla;
            }
            for (Food food : client.getFoods()) {
                if (food.renderX >= client.fpx1 && food.renderX <= client.fpx2 && food.renderY >= client.fpy1 && food.renderY <= client.fpy2) {
                    Color color = food.color;
                    float size = (food.size / 5.0F) * food.rad * 0.25F;
                    GL11.glPushMatrix();
                    GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), globalAlpha * food.fr);
                    GL11.glScalef(size, size, 1.0F);
                    float x = food.renderX / size;
                    float y = food.renderY / size;
                    drawTexture(x - 64.0F, y - 64.0F, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
                    GL11.glPopMatrix();
                }
            }
            for (Prey prey : client.getPreys()) {
                float posX = prey.posX + prey.fx;
                float posY = prey.posY + prey.fy;
                if (posX >= client.fpx1 && posX <= client.fpx2 && posY >= client.fpy1 && posY <= client.fpy2) {
                    Color color = prey.color;
                    float size = (prey.size / 10.0F) * prey.rad;
                    GL11.glPushMatrix();
                    GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), globalAlpha * prey.fr);
                    GL11.glScalef(size, size, 1.0F);
                    float x = posX / size;
                    float y = posY / size;
                    drawTexture(x - 64.0F, y - 64.0F, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
                    GL11.glPopMatrix();
                }
            }
            for (Snake snake : client.getSnakes()) {
                snake.isInView = true;
                    /*for (int t = snake.points.size() - 1; t >= 0; t--) {
                        SnakePoint point = snake.points.get(t);
                        float pointX = point.posX + point.fx;
                        float pointY = point.posY + point.fy;
                        if (pointX >= client.bpx1 && pointX <= client.bpx2 && pointY >= client.bpy1 && pointY <= client.bpy2) {
                            snake.isInView = true;
                            break;
                        }
                    }*/
            }
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            for (Snake<?> snake : client.getSnakes()) {
                float originX = snake.getRenderX(delta) + snake.fx;
                float originY = snake.getRenderY(delta) + snake.fy;
                float ehang = snake.ehang;
                float scale = snake.scale;
                if (snake.partSeparation != snake.wantedSeperation) {
                    if (snake.partSeparation < snake.wantedSeperation) {
                        snake.partSeparation += 0.01F;
                        if (snake.partSeparation >= snake.wantedSeperation) {
                            snake.partSeparation = snake.wantedSeperation;
                        }
                    } else if (snake.partSeparation > snake.wantedSeperation) {
                        snake.partSeparation -= 0.01F;
                        if (snake.partSeparation <= snake.wantedSeperation) {
                            snake.partSeparation = snake.wantedSeperation;
                        }
                    }
                }
                if (snake.isInView) {
                    List<Float> xs = new ArrayList<>();
                    List<Float> ys = new ArrayList<>();
                    float lastX;
                    float lastY;
                    float lastPointX;
                    float lastPointY;
                    float n = (snake.chl + snake.fchl) % 0.25F;
                    if (n < 0) {
                        n += 0.25F;
                    }
                    n = 0.25F - n;
                    float lastAverageX;
                    float lastAverageY;
                    float x = originX;
                    float y = originY;
                    float averageX = originX;
                    float averageY = originY;
                    float pointX = originX;
                    float pointY = originY;
                    float G = (float) (snake.cfl + (1.0F - Math.ceil((snake.chl + snake.fchl) / 0.25F) * 0.25F));
                    float K = 0;
                    float partSeparation = snake.partSeparation * client.qsm;
                    SkinColor[] pattern = snake.pattern;
                    for (int pointIndex = snake.points.size() - 1; pointIndex >= 0; pointIndex--) {
                        SnakePoint point = snake.points.get(pointIndex);
                        lastX = x;
                        lastY = y;
                        x = point.posX + point.fx;
                        y = point.posY + point.fy;
                        if (G > -0.25F) {
                            lastAverageX = averageX;
                            lastAverageY = averageY;
                            averageX = (x + lastX) / 2.0F;
                            averageY = (y + lastY) / 2.0F;
                            for (float q = 0.0F; q < 1.0F; q += 0.25F) {
                                float E = n + q;
                                float e = lastAverageX + (lastX - lastAverageX) * E;
                                float w = lastAverageY + (lastY - lastAverageY) * E;
                                float J = lastX + (averageX - lastX) * E;
                                float M = lastY + (averageY - lastY) * E;
                                lastPointX = pointX;
                                lastPointY = pointY;
                                pointX = e + (J - e) * E;
                                pointY = w + (M - w) * E;
                                if (G < 0) {
                                    pointX += -(lastPointX - pointX) * G / 0.25F;
                                    pointY += -(lastPointY - pointY) * G / 0.25F;
                                }
                                float partDistance = (float) Math.sqrt(Math.pow(pointX - lastPointX, 2) + Math.pow(pointY - lastPointY, 2));
                                if (K + partDistance < partSeparation) {
                                    K += partDistance;
                                } else {
                                    K = -K;
                                    for (int a = (int) ((partDistance - K) / partSeparation); a >= 1; a--) {
                                        K += partSeparation;
                                        float pax = lastPointX + (pointX - lastPointX) * K / partDistance;
                                        float pay = lastPointY + (pointY - lastPointY) * K / partDistance;
                                        if (true) {// pax >= client.bpx1 && pax <= client.bpx2 && pay >= client.bpy1 && pay <= client.bpy2) {
                                            xs.add(pax);
                                            ys.add(pay);
                                        }
                                    }
                                    K = partDistance - K;
                                }
                                if (G < 1.0F) {
                                    G -= 0.25F;
                                    if (G <= -0.25F) {
                                        break;
                                    }
                                }
                            }
                            if (G >= 1.0F) {
                                G--;
                            }
                        }
                    }
                    if (true) {// pointX >= client.bpx1 && pointX <= client.bpx2 && pointY >= client.bpy1 && pointY <= client.bpy2) {
                        xs.add(pointX);
                        ys.add(pointY);
                    }
                    textureManager.bindTexture("/textures/snake_point.png");
                    for (int pointIndex = xs.size() - 1; pointIndex >= 0; pointIndex--) {
                        pointX = (xs.get(pointIndex));
                        pointY = (ys.get(pointIndex));
                        SkinColor color = pattern[pointIndex % pattern.length];
                        float colorMultipler = 1.0F;
                        if (snake.speed > snake.accelleratingSpeed) {
                            float offset = (((pointIndex + client.ticks) / 2.0F) % 20.0F);
                            if (offset > 10.0F) {
                                offset = 10.0F - (offset - 10.0F);
                            }
                            colorMultipler += offset / 10.0F;
                        }
                        GL11.glColor4f(color.red * colorMultipler, color.green * colorMultipler, color.blue * colorMultipler, 1.0F);
                        GL11.glPushMatrix();
                        GL11.glTranslatef(pointX, pointY, 0);
                        float pointScale = snake.scale * 0.25F;
                        if (pointIndex < 4) {
                            pointScale *= 1 + (4 - pointIndex) * snake.headSwell;
                        }
                        GL11.glScalef(pointScale, pointScale, 1.0F);
                        drawTexture(-64, -64, 0, 0, 128, 128, 128, 128);
                        GL11.glPopMatrix();
                    }
                    if (snake.faceTexture == null && !snake.oneEye) {
                        GL11.glPushMatrix();
                        float eyeForward = 2.0F * scale;
                        float eyeSideDistance = 6.0F * scale;
                        GL11.glTranslatef(originX, originY, 0.0F);
                        float eyeOffsetX = (float) (Math.cos(ehang) * eyeForward + Math.cos(ehang - Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
                        float eyeOffsetY = (float) (Math.sin(ehang) * eyeForward + Math.sin(ehang - Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
                        drawCircle(eyeOffsetX, eyeOffsetY, snake.eyeRadius * scale, snake.eyeColor);
                        eyeOffsetX = (float) (Math.cos(ehang) * eyeForward + Math.cos(ehang + Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
                        eyeOffsetY = (float) (Math.sin(ehang) * eyeForward + Math.sin(ehang + Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
                        drawCircle(eyeOffsetX, eyeOffsetY, snake.eyeRadius * scale, snake.eyeColor);
                        eyeOffsetX = (float) (Math.cos(ehang) * (eyeForward + 0.5F) + snake.relativeEyeX * scale + Math.cos(ehang + Math.PI / 2.0F) * eyeSideDistance);
                        eyeOffsetY = (float) (Math.sin(ehang) * (eyeForward + 0.5F) + snake.relativeEyeY * scale + Math.sin(ehang + Math.PI / 2.0F) * eyeSideDistance);
                        drawCircle(eyeOffsetX, eyeOffsetY, scale * snake.pupilRadius, snake.pupilColor);
                        eyeOffsetX = (float) (Math.cos(ehang) * (eyeForward + 0.5F) + snake.relativeEyeX * scale + Math.cos(ehang - Math.PI / 2.0F) * eyeSideDistance);
                        eyeOffsetY = (float) (Math.sin(ehang) * (eyeForward + 0.5F) + snake.relativeEyeY * scale + Math.sin(ehang - Math.PI / 2.0F) * eyeSideDistance);
                        drawCircle(eyeOffsetX, eyeOffsetY, scale * snake.pupilRadius, snake.pupilColor);
                        GL11.glPopMatrix();
                    } else if (snake.faceTexture != null) {
                        GL11.glPushMatrix();
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        GL11.glTranslatef(originX, originY, 0.0F);
                        float faceScale = 0.2F;
                        GL11.glScalef(snake.scale * faceScale, snake.scale * faceScale, 1.0F);
                        GL11.glRotatef((float) Math.toDegrees(snake.angle + (snake.eyeAngle / 10.0F)), 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-5.0F / faceScale, 0.0F, 0.0F);
                        textureManager.bindTexture("/textures/" + snake.faceTexture + ".png");
                        drawTexture(-64.0F, -64.0F, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
                        GL11.glPopMatrix();
                    }
                    if (snake.name.length() > 0) {
                        drawCenteredString(snake.name, originX, originY + (32 * snake.scale), snake.scale, 0xFFFFFF);
                    }
                    if (snake.antenna) {
                        GL11.glPushMatrix();
                        float directionX = (float) Math.cos(snake.angle);
                        float directionY = (float) Math.sin(snake.angle);
                        pointX = originX - 8 * directionX * snake.scale;
                        pointY = originY - 8 * directionY * snake.scale;
                        snake.antennaX[0] = pointX;
                        snake.antennaY[0] = pointY;
                        float antennaScale = snake.scale;
                        int antennaLength = snake.antennaX.length - 1;
                        if (!snake.antennaShown) {
                            snake.antennaShown = true;
                            for (int i = 1; i <= antennaLength; i++) {
                                snake.antennaX[i] = pointX - directionX * i * 4 * snake.scale;
                                snake.antennaY[i] = pointY - directionY * i * 4 * snake.scale;
                            }
                        }
                        for (int i = 1; i <= antennaLength; i++) {
                            x = (float) (snake.antennaX[i - 1] + (Math.random() * 2.0F - 1));
                            y = (float) (snake.antennaY[i - 1] + (Math.random() * 2.0F - 1));
                            float diffX = snake.antennaX[i] - x;
                            float diffY = snake.antennaY[i] - y;
                            float ang = (float) Math.atan2(diffY, diffX);
                            x += Math.cos(ang) * snake.scale * 4.0F;
                            y += Math.sin(ang) * snake.scale * 4.0F;
                            snake.antennaVelocityX[i] += (x - snake.antennaX[i]) * 0.1F;
                            snake.antennaVelocityY[i] += (y - snake.antennaY[i]) * 0.1F;
                            snake.antennaX[i] += snake.antennaVelocityX[i];
                            snake.antennaY[i] += snake.antennaVelocityY[i];
                            snake.antennaVelocityX[i] *= 0.88F;
                            snake.antennaVelocityY[i] *= 0.88F;
                            diffX = snake.antennaX[i] - snake.antennaX[i - 1];
                            diffY = snake.antennaY[i] - snake.antennaY[i - 1];
                            float J = (float) Math.sqrt(diffX * diffX + diffY * diffY);
                            if (J > snake.scale * 4.0F) {
                                ang = (float) Math.atan2(diffY, diffX);
                                snake.antennaX[i] = (float) (snake.antennaX[i - 1] + Math.cos(ang) * 4 * snake.scale);
                                snake.antennaY[i] = (float) (snake.antennaY[i - 1] + Math.sin(ang) * 4 * snake.scale);
                            }
                        }
                        antennaLength = snake.antennaX.length;
                        float prevX = snake.antennaX[antennaLength - 1];
                        float prevY = snake.antennaY[antennaLength - 1];
                        beginConnectedLines(antennaScale * 5.0F, snake.antennaPrimaryColor);
                        for (int i = 0; i < antennaLength; i++) {
                            x = snake.antennaX[i];
                            y = snake.antennaY[i];
                            if (Math.abs(x - prevX) + Math.abs(y - prevY) >= 1) {
                                drawConnectedLine(prevX, prevY, x, y);
                                prevX = x;
                                prevY = y;
                            }
                        }
                        endConnectedLines();
                        beginConnectedLines(antennaScale * 4.0F, snake.antennaSecondaryColor);
                        for (int i = 0; i < antennaLength; i++) {
                            x = snake.antennaX[i];
                            y = snake.antennaY[i];
                            if (Math.abs(x - prevX) + Math.abs(y - prevY) >= 1) {
                                drawConnectedLine(prevX, prevY, x, y);
                                prevX = x;
                                prevY = y;
                            }
                        }
                        endConnectedLines();
                        if (snake.antennaTexture != null) {
                            GL11.glTranslatef(snake.antennaX[antennaLength - 1], snake.antennaY[antennaLength - 1], 0.0F);
                            if (snake.antennaBottomRotate) {
                                float bottomAngle = (float) (Math.atan2(snake.antennaY[antennaLength - 1] - snake.antennaY[antennaLength - 2], snake.antennaX[antennaLength - 1] - snake.antennaX[antennaLength - 2]) - snake.antennaBottomAngle);
                                if (bottomAngle < 0 || bottomAngle >= SlytherClient.PI_2) {
                                    bottomAngle %= SlytherClient.PI_2;
                                }
                                if (bottomAngle < -Math.PI) {
                                    bottomAngle += SlytherClient.PI_2;
                                } else {
                                    if (bottomAngle > Math.PI) {
                                        bottomAngle -= SlytherClient.PI_2;
                                    }
                                }
                                snake.antennaBottomAngle = (float) ((snake.antennaBottomAngle + 0.15F * bottomAngle) % SlytherClient.PI_2);
                                GL11.glRotatef((float) Math.toDegrees(snake.antennaBottomAngle), 0.0F, 0.0F, 1.0F);
                            }
                            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                            antennaScale = snake.scale * snake.antennaScale * 0.25F;
                            GL11.glScalef(antennaScale, antennaScale, 1.0F);
                            textureManager.bindTexture("/textures/" + snake.antennaTexture + ".png");
                            drawTexture(-64.0F, -64.0F, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
                        }
                        GL11.glPopMatrix();
                    }
                }
            }

            GL11.glPopMatrix();

            drawString("Your length: " + player.getLength(), 3.0F, renderResolution.getHeight() - 35.0F, 1.0F, 0xFFFFFF);
            drawString("Rank " + client.rank + "/" + client.snakeCount, 3.0F, renderResolution.getHeight() - 18.0F, 1.0F, 0xAAAAAA);

            drawLargeString("Leaderboard:", renderResolution.getWidth() - largeFont.getWidth("Leaderboard:") - 10.0F, 2.0F, 1.0F, 0xFFFFFF);

            int leaderboardY = largeFont.getHeight() + 4;

            List<LeaderboardEntry> leaderboard = new ArrayList<>(client.leaderboard);

            int alpha = 255;
            int alphaChange = (int) ((leaderboard.size() / 10.0F) * 20);

            for (int i = 1; i <= leaderboard.size(); i++) {
                LeaderboardEntry leaderboardEntry = leaderboard.get(i - 1);
                String text = i + ". " + leaderboardEntry;
                drawString(text, renderResolution.getWidth() - font.getWidth(text) - 8.0F, leaderboardY, 1.0F, leaderboardEntry.color.toHex() | alpha << 24);

                leaderboardY += font.getHeight() + 2;
                alpha -= alphaChange;
            }

            float mapX = renderResolution.getWidth() - 100.0F + 40.0F;
            float mapY = renderResolution.getHeight() - 100.0F + 40.0F;
            drawCircle(mapX, mapY, 42.5F, 0x222222);
            drawCircle(mapX, mapY, 40.0F, 0x555555);
            GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
            for (int x = 0; x < 80; x++) {
                for (int y = 0; y < 80; y++) {
                    if (client.map[x][y]) {
                        drawRect((renderResolution.getWidth() - 100.0F) + x, (renderResolution.getHeight() - 100.0F) + y, 1.0F, 1.0F);
                    }
                }
            }
            float locationMarkerX = (renderResolution.getWidth() - 100.0F) + Math.round((client.player.posX - client.GAME_RADIUS) * 40 / client.GAME_RADIUS + 52 - 7);
            float locationMarkerY = (renderResolution.getHeight() - 100.0F) + Math.round((client.player.posY - client.GAME_RADIUS) * 40 / client.GAME_RADIUS + 52 - 7);
            drawCircle(locationMarkerX, locationMarkerY, 3.0F, 0x202020);
            drawCircle(locationMarkerX, locationMarkerY, 2.0F, 0xFFFFFF);
        } else {
            GL11.glPopMatrix();
            drawCenteredLargeString("Connecting to server...", renderResolution.getWidth() / 2.0F, renderResolution.getHeight() / 2.0F, 2.0F, 0xFFFFFF);
        }
    }

    @Override
    public void update() {
        client.zoomOffset += Mouse.getDWheel() * 0.0005F;
        if (client.zoomOffset > 1.0F) {
            client.zoomOffset = 1.0F;
        } else if (client.zoomOffset < -0.75F) {
            client.zoomOffset = -0.75F;
        }
    }

    @Override
    public void keyPressed(int key, char character) {
        if (key == Keyboard.KEY_BACK || key == Keyboard.KEY_ESCAPE) {
            client.networkManager.close(1000, "Forcefully closed by player");
            client.player = null;
            client.networkManager = null;
            closeGui();
            client.openGui(new GuiMainMenu());
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {

    }
}
