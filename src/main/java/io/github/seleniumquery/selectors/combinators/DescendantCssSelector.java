package io.github.seleniumquery.selectors.combinators;

import io.github.seleniumquery.functions.ClosestFunction;
import io.github.seleniumquery.selector.CompiledCssSelector;
import io.github.seleniumquery.selector.CssFilter;
import io.github.seleniumquery.selector.CssSelector;
import io.github.seleniumquery.selector.CssSelectorCompilerService;
import io.github.seleniumquery.selector.CssSelectorMatcherService;
import io.github.seleniumquery.selector.SelectorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.w3c.css.sac.DescendantSelector;

public class DescendantCssSelector implements CssSelector<DescendantSelector> {

	private static final DescendantCssSelector instance = new DescendantCssSelector();
	public static DescendantCssSelector getInstance() {
		return instance;
	}
	private DescendantCssSelector() { }
	
	/**
	 * E F
	 */
	@Override
	public boolean is(WebDriver driver, WebElement element, Map<String, String> stringMap, DescendantSelector descendantSelector) {
		if (CssSelectorMatcherService.elementMatchesSelector(driver, element, stringMap, descendantSelector.getSimpleSelector())) {
	
			WebElement ancestor = SelectorUtils.parent(element);
	
			while (ancestor != null) {
				if (CssSelectorMatcherService.elementMatchesSelector(driver, ancestor, stringMap, descendantSelector.getAncestorSelector())) {
					return true;
				}
				ancestor = SelectorUtils.parent(ancestor);
			}
		}
		return false;
	}

	@Override
	public CompiledCssSelector compile(WebDriver driver, Map<String, String> stringMap, DescendantSelector descendantSelector) {
		CompiledCssSelector childrenCompiled = CssSelectorCompilerService.compileSelector(driver, stringMap, descendantSelector.getSimpleSelector());
		CompiledCssSelector ancestorCompiled = CssSelectorCompilerService.compileSelector(driver, stringMap, descendantSelector.getAncestorSelector());
		
		CssFilter descendantFilter = new DescendantFilter(childrenCompiled, ancestorCompiled);
		return new CompiledCssSelector(ancestorCompiled.getCssSelector()+" "+childrenCompiled.getCssSelector(), descendantFilter);
	}
	
	
	private static final class DescendantFilter implements CssFilter {
		private final CompiledCssSelector childrenCompiled;
		private final CompiledCssSelector ancestorCompiled;
		
		private DescendantFilter(CompiledCssSelector childrenCompiled, CompiledCssSelector ancestorCompiled) {
			this.childrenCompiled = childrenCompiled;
			this.ancestorCompiled = ancestorCompiled;
		}
		
		@Override
		public List<WebElement> filter(WebDriver driver, List<WebElement> elements) {
			elements = childrenCompiled.filter(driver, elements);
			
			outerFor:for (Iterator<WebElement> iterator = elements.iterator(); iterator.hasNext();) {
				WebElement element = iterator.next();
				
				// closest() starts in the element, we dont want that because we are testing the parent on the descendant selector
				WebElement startingElement = SelectorUtils.parent(element);
				
				WebElement matchingAncestor = ClosestFunction.closest(driver, startingElement, ancestorCompiled.getCssSelector());
				while (matchingAncestor != null) {
					
					List<WebElement> mas = ancestorCompiled.filter(driver, new ArrayList<WebElement>(Arrays.asList(matchingAncestor)));
					boolean theMatchedAncestorMatchesTheFilter = !mas.isEmpty();
					if (theMatchedAncestorMatchesTheFilter) {
						continue outerFor; // this element's ancestor is ok, keep it, continue to next element
					}
					
					// walks up one step, otherwise closest will match the same element again
					matchingAncestor = SelectorUtils.parent(matchingAncestor);
					
					matchingAncestor = ClosestFunction.closest(driver, matchingAncestor, ancestorCompiled.getCssSelector());
				}
				iterator.remove();
			}
			return elements;
		}
	}

}