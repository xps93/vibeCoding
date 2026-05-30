import { computed } from 'vue'
import { useMediaQuery } from '@vueuse/core'

export function useMobile() {
  const isMobile = useMediaQuery('(max-width: 767px)')
  const isTablet = useMediaQuery('(min-width: 768px) and (max-width: 1199px)')
  const isDesktop = useMediaQuery('(min-width: 1200px)')

  const showLeftSidebar = computed(() => isDesktop.value || isTablet.value)
  const showRightPanel = computed(() => isDesktop.value)

  const leftDrawer = computed(() => isMobile.value)
  const rightDrawer = computed(() => isTablet.value || isMobile.value)

  return { isMobile, isTablet, isDesktop, showLeftSidebar, showRightPanel, leftDrawer, rightDrawer }
}
