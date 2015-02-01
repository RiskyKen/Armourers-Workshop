package riskyken.armourersWorkshop.client.render.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockMannequin extends TileEntitySpecialRenderer {
    
    private ModelMannequin model;
    private RenderPlayer renderPlayer;
    private final Minecraft mc;
    
    public RenderBlockMannequin() {
        renderPlayer = (RenderPlayer) RenderManager.instance.entityRenderMap.get(EntityPlayer.class);
        mc = Minecraft.getMinecraft();
        model = new ModelMannequin();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {
        mc.mcProfiler.startSection("armourers mannequin");
        TileEntityMannequin te = (TileEntityMannequin) tileEntity;
        MannequinFakePlayer fakePlayer = te.getFakePlayer();
        
        double heightOffset = te.getHeightOffset();
        //heightOffset = 12;
        ModelBiped targetBiped = renderPlayer.modelBipedMain;
        if (Loader.isModLoaded("moreplayermodels")) {
            targetBiped = model;
        }
        
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_NORMALIZE);
        float scale = 0.0625F;
        
        int rotaion = te.getRotation();
        
        GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
        GL11.glScalef(scale * 15, scale * 15, scale * 15);
        GL11.glTranslated(0, scale * -1.6F, 0);
        
        GL11.glScalef(-1, -1, 1);
        GL11.glRotatef(rotaion * 22.5F, 0, 1, 0);
        
        if (te.getIsDoll()) {
            float dollScale = 0.5F;
            GL11.glScalef(dollScale, dollScale, dollScale);
            GL11.glTranslatef(0, scale * 24, 0);
        }
        
        if (te.getGameProfile() != null) {
            if (te.getGameProfile() != null & te.getWorldObj() != null) {
                if (fakePlayer == null) {
                    fakePlayer = new MannequinFakePlayer(te.getWorldObj(),te.getGameProfile());
                    fakePlayer.posX = x;
                    fakePlayer.posY = y;
                    fakePlayer.posZ = z;
                    fakePlayer.prevPosX = x;
                    fakePlayer.prevPosY = y;
                    fakePlayer.prevPosZ = z;
                    
                    te.setFakePlayer(fakePlayer);
                }
            }
        }
        
        float f6 = 2.0F;
        if (fakePlayer != null) {
            targetBiped.isChild = te.getBipedRotations().isChild;
            fakePlayer.rotationPitch = (float) Math.toDegrees(te.getBipedRotations().head.rotationX);
            fakePlayer.prevRotationPitch = (float) Math.toDegrees(te.getBipedRotations().head.rotationX);
            fakePlayer.rotationYawHead = (float) Math.toDegrees(te.getBipedRotations().head.rotationY);
            fakePlayer.prevRotationYawHead = (float) Math.toDegrees(te.getBipedRotations().head.rotationY);
            fakePlayer.onUpdate();
            
            fakePlayer.ticksExisted = Minecraft.getMinecraft().thePlayer.ticksExisted;
            
            if (te.getBipedRotations() != null) {
                te.getBipedRotations().applyRotationsToBiped(targetBiped);
            }
            
            //Pre render events
            RenderPlayerEvent.Pre preEvent = new RenderPlayerEvent.Pre(fakePlayer, renderPlayer, tickTime);
            RenderPlayerEvent.Specials.Pre preEventSpecials = new RenderPlayerEvent.Specials.Pre(fakePlayer, renderPlayer, tickTime);

            if (targetBiped.isChild) {
                GL11.glPushMatrix();
                GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
                GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            }
            GL11.glDisable(GL11.GL_CULL_FACE);
            MinecraftForge.EVENT_BUS.post(preEvent);
            MinecraftForge.EVENT_BUS.post(preEventSpecials);
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (targetBiped.isChild) {
                GL11.glPopMatrix();
            }
        }
        
        SkinHelper.bindPlayersNormalSkin(te.getGameProfile());
        
        ApiRegistrar.INSTANCE.onRenderMannequin(tileEntity, te.getGameProfile());
        
        targetBiped.bipedRightArm.setRotationPoint(-5.0F, 2.0F , 0.0F);
        targetBiped.bipedLeftArm.setRotationPoint(5.0F, 2.0F , 0.0F);
        targetBiped.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        targetBiped.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        targetBiped.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        targetBiped.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        
        te.getBipedRotations().applyRotationsToBiped(targetBiped);
        
        if (te.getBipedRotations().isChild) {
            GL11.glTranslated(0, heightOffset * scale * 1.0F / f6, 0);
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
            GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            targetBiped.bipedHead.render(scale);
            GL11.glDisable(GL11.GL_CULL_FACE);
            targetBiped.bipedHeadwear.render(scale);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);
            targetBiped.bipedBody.render(scale);
            targetBiped.bipedRightArm.render(scale);
            targetBiped.bipedLeftArm.render(scale);
            targetBiped.bipedRightLeg.render(scale);
            targetBiped.bipedLeftLeg.render(scale);
            GL11.glPopMatrix();
        } else {
            GL11.glTranslated(0, heightOffset * scale, 0);
            targetBiped.bipedHead.render(scale);
            targetBiped.bipedBody.render(scale);
            targetBiped.bipedRightArm.render(scale);
            targetBiped.bipedLeftArm.render(scale);
            targetBiped.bipedRightLeg.render(scale);
            targetBiped.bipedLeftLeg.render(scale);
            GL11.glDisable(GL11.GL_CULL_FACE);
            targetBiped.bipedHeadwear.render(scale);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
        
        //Post render events
        if (fakePlayer != null) {
            RenderPlayerEvent.Post postEvent = new RenderPlayerEvent.Post(fakePlayer, renderPlayer, tickTime);
            RenderPlayerEvent.Specials.Post postEvenSpecialst = new RenderPlayerEvent.Specials.Post(fakePlayer, renderPlayer, tickTime);
            if (targetBiped.isChild) {
                GL11.glPushMatrix();
                GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
                GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            }
            GL11.glDisable(GL11.GL_CULL_FACE);
            MinecraftForge.EVENT_BUS.post(postEvent);
            MinecraftForge.EVENT_BUS.post(postEvenSpecialst);
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (targetBiped.isChild) {
                GL11.glPopMatrix();
            }
        }
        
        //Render armourer's skins
        mc.mcProfiler.startSection("mannequin skin");
        EquipmentModelRenderer.INSTANCE.renderMannequinEquipment(((TileEntityMannequin)tileEntity), targetBiped);
        mc.mcProfiler.endSection();
        
        //Render items.
        renderEquippedItems(te, fakePlayer);
        
        targetBiped.bipedLeftLeg.rotateAngleZ = 0F;
        targetBiped.bipedRightLeg.rotateAngleZ = 0F;
        targetBiped.bipedHead.rotateAngleZ = 0F;
        targetBiped.bipedHeadwear.rotateAngleZ = 0F;
        
        GL11.glDisable(GL11.GL_NORMALIZE);
        GL11.glPopMatrix();
        mc.mcProfiler.endSection();
    }
    
    private void renderEquippedItems(IInventory inventory, MannequinFakePlayer fakePlayer) {
        RenderItem ri = (RenderItem) RenderManager.instance.entityRenderMap.get(EntityItem.class);
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null & fakePlayer != null) {
                renderEquippedItem(fakePlayer, stack);
            }
        }
    }
    
    private void renderEquippedItem(MannequinFakePlayer fakePlayer, ItemStack stack) {
        GL11.glPushMatrix();
        if (!(stack.getItem() instanceof ItemBlock)) {
            //GL11.glTranslatef(0.9375F, 0.0625F, 0.0F);
            GL11.glRotatef(335.0F, 0.0F, 0.0F, -1.0F);
            GL11.glRotatef(50.0F, 0.0F, -1.0F, 0.0F);
            GL11.glScalef(0.75F, 0.75F, 0.75F);
        }

        RenderManager rm = RenderManager.instance;
        rm.itemRenderer.renderItem(fakePlayer, stack, 0);
        GL11.glPopMatrix();
    }
}
