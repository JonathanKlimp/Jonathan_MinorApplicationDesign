$(document).ready(() => {
    const $mainSection = $("#main-section");
    const $header = $("#header");

    $mainSection.css({
       "height": "calc(100% - " + $header.outerHeight() + "px)"
    });
});