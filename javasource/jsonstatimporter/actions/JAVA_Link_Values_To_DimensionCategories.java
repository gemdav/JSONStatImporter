// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package jsonstatimporter.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixIdentifier;
import com.mendix.webui.CustomJavaAction;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import jsonstatimporter.proxies.*;

public class JAVA_Link_Values_To_DimensionCategories extends CustomJavaAction<java.lang.Void>
{
	/** @deprecated use JSONStat.getMendixObject() instead. */
	@java.lang.Deprecated(forRemoval = true)
	private final IMendixObject __JSONStat;
	private final jsonstatimporter.proxies.JSONStat JSONStat;

	public JAVA_Link_Values_To_DimensionCategories(
		IContext context,
		IMendixObject _jSONStat
	)
	{
		super(context);
		this.__JSONStat = _jSONStat;
		this.JSONStat = _jSONStat == null ? null : jsonstatimporter.proxies.JSONStat.initialize(getContext(), _jSONStat);
	}

	@java.lang.Override
	public java.lang.Void executeAction() throws Exception
	{
		// BEGIN USER CODE
		List<IMendixObject> valuesImo = Core.retrieveByPath(getContext(), JSONStat.getMendixObject(), "JSONStatImporter.Value_JSONStat");
		List<Value> values = valuesImo.stream().map(valueImo -> safeValueLoad(getContext(), valueImo.getId())).collect(Collectors.toList());
		List<IMendixObject> dimensionsImo = Core.retrieveByPath(getContext(), JSONStat.getMendixObject(), "JSONStatImporter.Dimension_JSONStat");
		List<Dimension> dimensions = dimensionsImo.stream().map(dimensionImo -> safeDimensionLoad(getContext(), dimensionImo.getId())).collect(Collectors.toList());

		for (Value value : values) {
			long linearIndex = value.getIndex();
		
			List<Integer> categoryIndexes = resolveLinearIndex(dimensions, linearIndex);
			for(int i = 0; i < dimensions.size(); i++) {
				Dimension dimension = dimensions.get(i);
				int categoryIndex = categoryIndexes.get(i);
				List<IMendixObject> categoriesImo = Core.retrieveByPath(getContext(), dimension.getMendixObject(), "JSONStatImporter.Category_Dimension");
				List<Category> categories = categoriesImo.stream().map(categoryImo -> safeCategoryLoad(getContext(), categoryImo.getId())).collect(Collectors.toList());
				Category category = resolveCategory(categories, categoryIndex);
				List<Category> currentCategories = value.getValue_Category();
				currentCategories.add(category);
				value.setValue_Category(currentCategories);
			}
		}
		return null;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "JAVA_Link_Values_To_DimensionCategories";
	}

	// BEGIN EXTRA CODE
	private Value safeValueLoad(IContext context, IMendixIdentifier identifier) {
		try {
			return Value.load(context, identifier);
		} catch (CoreException ce) {
			throw new RuntimeException(ce);
		}
	}

	private Dimension safeDimensionLoad(IContext context, IMendixIdentifier identifier) {
		try {
			return Dimension.load(context, identifier);
		} catch (CoreException ce) {
			throw new RuntimeException(ce);
		}
	}

	private Category safeCategoryLoad(IContext context, IMendixIdentifier identifier) {
		try {
			return Category.load(context, identifier);
		} catch (CoreException ce) {
			throw new RuntimeException(ce);
		}
	}

	private static List<Integer> resolveLinearIndex(List<Dimension> dimensionSizes, long linearIndex) {
		List<Integer> categoryIndexes = new ArrayList<>();
        long totalSize = 1;
        for (int i = dimensionSizes.size() - 1; i >= 0; i--) {
			int dimensionSize = dimensionSizes.get(i).getSize();
            totalSize *= dimensionSize;
            int currentIndex = (int) (linearIndex % totalSize / (totalSize / dimensionSize));
            categoryIndexes.add(0, currentIndex);
        }
        return categoryIndexes;
	}

	private static Category resolveCategory(List<Category> categories, int index) {
		for (Category category : categories) {
			if (category.getIndex().equals(index)) {
				return category;
			}
		}
		return null;
	}
	// END EXTRA CODE
}
