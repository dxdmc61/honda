/**
 * Side Navigation Component JavaScript
 * Styled to match Search Claims component
 */
(function($, Granite) {
   "use strict";
   
   var selectors = {
       self: "[data-cmp-is='globalNavigation']",
       item: ".cmp-side-navigation__item",
       toggleButton: ".cmp-side-navigation__toggle-button",
       list: ".cmp-side-navigation__list"
   };
   
   function SideNavigation(element) {
       var that = this;
       
       function init() {
           that.element = element;
           
           // Bind events
           bindEvents();
           
           // Initialize component
           initializeComponent();
           
           // Log initialization in author mode
           if (Granite && Granite.author && Granite.author.MessageChannel) {
               console.log("Side Navigation Component initialized");
           }
       }
       
       function bindEvents() {
           // Toggle button click
           $(that.element).on("click", selectors.toggleButton, function(e) {
               e.preventDefault();
               e.stopPropagation();
               
               var $item = $(this).closest(selectors.item);
               var isExpanded = $item.attr("data-cmp-expanded") === "true";
               
               // Toggle expanded state
               $item.attr("data-cmp-expanded", !isExpanded);
               $(this).attr("aria-expanded", !isExpanded);
               
               // Animate smoothly
               if (!isExpanded) {
                   $item.children(selectors.list).slideDown(200);
               } else {
                   $item.children(selectors.list).slideUp(200);
               }
           });
           
           // Optional: Add keyboard navigation support
           $(that.element).on("keydown", selectors.toggleButton, function(e) {
               // Handle Enter and Space key
               if (e.which === 13 || e.which === 32) {
                   e.preventDefault();
                   $(this).trigger("click");
               }
           });
       }
       
       function initializeComponent() {
           // Set initial aria-expanded attribute values
           $(that.element).find(selectors.toggleButton).each(function() {
               var $item = $(this).closest(selectors.item);
               var isExpanded = $item.attr("data-cmp-expanded") === "true";
               $(this).attr("aria-expanded", isExpanded);
               
               // Make sure expanded items' lists are visible initially (without animation)
               if (isExpanded) {
                   $item.children(selectors.list).show();
               }
           });
           
           // Add accessible labels
           $(that.element).find(selectors.item).each(function() {
               var $link = $(this).children(".cmp-side-navigation__link");
               var $button = $(this).children(selectors.toggleButton);
               
               if ($button.length && $link.length) {
                   var title = $link.text().trim();
                   $button.attr("aria-label", "Toggle " + title + " submenu");
               }
           });
       }
       
       init();
   }
   
   // Initialize on document ready
   $(document).ready(function() {
       $(selectors.self).each(function() {
           new SideNavigation(this);
       });
   });
   
   // Initialize when Granite UI adds the component
   $(document).on("foundation-contentloaded", function() {
       $(selectors.self).each(function() {
           if (!this.hasNavigation) {
               new SideNavigation(this);
               this.hasNavigation = true;
           }
       });
   });
   
})(jQuery, Granite);