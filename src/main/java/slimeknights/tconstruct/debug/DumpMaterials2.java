package slimeknights.tconstruct.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.FletchingMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.traits.ITrait;

public class DumpMaterials2 extends CommandBase {
  private static final Logger log = TConstruct.log;

  @Override
  public String getName() {
    return "tic_dump_materials";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "/tic_dump_materials";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    int mat_count;
    int trait_count;

    try (PrintWriter out = new PrintWriter(new File("dumps/materials.txt"))) {
      mat_count = writeMaterials(out);
    } catch (FileNotFoundException e) {
      throw new CommandException(e.getMessage());
    }

    try (PrintWriter out = new PrintWriter(new File("dumps/traits.txt"))) {
      trait_count = writeTraitDescriptions(out);
    } catch (FileNotFoundException e) {
      throw new CommandException(e.getMessage());
    }

    sender.sendMessage(new TextComponentString(String.format("exported %d materials and %d traits", mat_count, trait_count)));
  }

  private int writeMaterials(PrintWriter out) {
    int i = 0;
    for (Material material : TinkerRegistry.getAllMaterials()) {
      i += 1;
      out.format("%s (%s)\n", material.getLocalizedName(), material.getIdentifier());

      HeadMaterialStats head = material.getStats(MaterialTypes.HEAD);
      writeTraits(out, material, MaterialTypes.HEAD);
      out.format("Head Durability: %d\n", head == null ? null : head.durability);
      out.format("Head Mining Speed: %f\n", head == null ? null : head.miningspeed);
      out.format("Head Attack: %f\n", head == null ? null : head.attack);
      out.format("Head Harvest Level: %d\n", head == null ? null : head.harvestLevel);

      HandleMaterialStats handle = material.getStats(MaterialTypes.HANDLE);
      writeTraits(out, material, MaterialTypes.HANDLE);
      out.format("Handle Durability: %d\n", handle == null ? null : handle.durability);
      out.format("Handle Modifier: %f\n", handle == null ? null : handle.modifier);

      ExtraMaterialStats extra = material.getStats(MaterialTypes.EXTRA);
      writeTraits(out, material, MaterialTypes.EXTRA);
      out.format("Extra Durability: %d\n", extra == null ? null : extra.extraDurability);

      BowMaterialStats bow = material.getStats(MaterialTypes.BOW);
      writeTraits(out, material, MaterialTypes.BOW);
      out.format("Bow Draw Speed: %f\n", bow == null ? null : bow.drawspeed);
      out.format("Bow Range: %f\n", bow == null ? null : bow.range);
      out.format("Bow Bonus Damage: %f\n", bow == null ? null : bow.bonusDamage);

      BowStringMaterialStats string = material.getStats(MaterialTypes.BOWSTRING);
      writeTraits(out, material, MaterialTypes.BOWSTRING);
      out.format("BowString Modifier: %f\n", string == null ? null : string.modifier);
      
      ArrowShaftMaterialStats shaft = material.getStats(MaterialTypes.SHAFT);
      writeTraits(out, material, MaterialTypes.SHAFT);
      out.format("Shaft Modifier: %f\n", shaft == null ? null : shaft.modifier);
      out.format("Shaft Bonus Ammo: %d\n", shaft == null ? null : shaft.bonusAmmo);

      FletchingMaterialStats fletching = material.getStats(MaterialTypes.FLETCHING);
      writeTraits(out, material, MaterialTypes.FLETCHING);
      out.format("Fletching Modifier: %f\n", fletching == null ? null : fletching.modifier);
      out.format("Fletching Accuracy: %f\n", fletching == null ? null : fletching.accuracy);

      out.append('\n');
    }

    return i;
  }

  private void writeTraits(PrintWriter out, Material material, String type) {
    for (ITrait trait : material.getAllTraitsForStats(type)) {
      out.format("%s Trait: %s (%s)\n", StringUtils.capitalize(type), trait.getLocalizedName(), trait.getIdentifier());
    }
  }

  private int writeTraitDescriptions(PrintWriter out) {
    int i = 0;

    HashSet<ITrait> traits = new HashSet<ITrait>();
    for (Material material : TinkerRegistry.getAllMaterials()) {
      traits.addAll(material.getAllTraits());
    }

    for (ITrait trait : traits) {
      i += 1;
      String desc = net.minecraft.util.StringUtils.stripControlCodes(trait.getLocalizedDesc())
        .replace("\r", " ") // dont think carriage return appears anywhere but just in case
        .replace("\\r", " ")
        .replace("\n", " ")
        .replace("\\n", " ");
      out.format("%s (%s): %s\n", trait.getLocalizedName(), trait.getIdentifier(), desc);
    }

    return i;
  }
}
