import { Component } from "@angular/core";
import { MenuItem } from "primeng/api";
import { UserDetails } from "src/app/model/UserDetails";
import { SessionService } from "src/app/service/session.service";

@Component({
  selector: "app-lookup-menu",
  templateUrl: "./lookup-menu.component.html",
})
export class LookupMenuComponent {
  tieredItems: MenuItem[];
  userDetails: UserDetails;

  constructor(private sessionService: SessionService) {}

  ngOnInit() {
    this.userDetails = this.sessionService.getUserDetailsObject();

    for (const item of this.userDetails.menus) {
    
      if (item.label === "Configurations") {
        for (const itm of item.items) {
          if (itm.label === "Lookups") {
            this.tieredItems = itm.items;
            for (const i of itm.items) {
              console.info( JSON.stringify(i.label));
            }
           
            break;
          }
        }
        break;
      }
    }

    //this.tieredItems = this.userDetails.menus;
    console.info("Menus: " + JSON.stringify(this.userDetails.menus));

    /**this.tieredItems = [
       
        {
            label: 'Shipments',
            icon: 'pi pi-fw pi-envelope',
            items: [
                {
                    label: 'Tracker',
                    icon: 'pi pi-fw pi-compass',

                },
                {
                    label: 'Map',
                    icon: 'pi pi-fw pi-map-marker',

                },
                {
                    label: 'Manage',
                    icon: 'pi pi-fw pi-pencil'
                }
            ]
        },
        {
            label: 'Profile',
            icon: 'pi pi-fw pi-user',
            items: [
                {
                    label: 'Settings',
                    icon: 'pi pi-fw pi-cog'
                },
                {
                    label: 'Billing',
                    icon: 'pi pi-fw pi-file'
                }
            ]
        },
        { separator: true },
        {
            label: 'Quit',
            icon: 'pi pi-fw pi-sign-out'
        }
    ];*/
  }
}
