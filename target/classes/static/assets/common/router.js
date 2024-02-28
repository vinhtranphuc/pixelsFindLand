var PAGE_TITLE = "Work Spores";

// create document click that watches the nav links only
document.addEventListener("click", (e) => {
    const { target } = e;
    if (!target.matches("nav a")) {
        return;
    }
    e.preventDefault();
    urlRoute();
});

// create an object that maps the url to the template, title, and description
const pageRouters = {
    "/": {
        headTitle: "Home | " + PAGE_TITLE,
        pageTitle: "Main"
    },
    "/test": {
        headTitle: "Test | " + PAGE_TITLE,
        pageTitle: "Test"
    },
};

// create a function that watches the url and calls the urlLocationHandler
const urlRoute = (event) => {
    event = event || window.event; // get window.event if event argument not provided
    event.preventDefault();
    // window.history.pushState(state, unused, target link);
    window.history.pushState({}, "", event.target.href);
    urlLocationHandler();
};

// create a function that handles the url location
const urlLocationHandler = async () => {
    const location = window.location.pathname; // get the url path
    // if the path length is 0, set it to primary page route
    if (location.length == 0) {
        location = "/";
    }
    // get the route object from the pageRouters object
    const route = pageRouters[location] || pageRouters["404"];
    // get the html from the template
    const html = await fetch(route.viewPath).then((response) => response.text());
    // set the content of the content div to the html
    document.getElementById("content").innerHTML = html;
    // set the title of the document to the title of the route
    document.title = route.title;
    // set the description of the document to the description of the route
    document
        .querySelector('meta[name="description"]')
        .setAttribute("content", route.description);
};

// add an event listener to the window that watches for url changes
window.onpopstate = urlLocationHandler;
// call the urlLocationHandler function to handle the initial url
window.route = urlRoute;
// call the urlLocationHandler function to handle the initial url
urlLocationHandler();
