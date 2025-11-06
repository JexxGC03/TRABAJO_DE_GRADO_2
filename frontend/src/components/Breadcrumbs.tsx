import { ChevronRight, Home } from 'lucide-react'

interface BreadcrumbsProps {
  items: { label: string; onClick?: () => void }[]
}

export function Breadcrumbs({ items }: BreadcrumbsProps) {
  return (
    <nav className="flex items-center gap-2 text-sm mb-4">
      {items.map((item, index) => (
        <div key={index} className="flex items-center gap-2">
          {index === 0 && <Home className="w-4 h-4 text-primary" />}
          {item.onClick ? (
            <button
              onClick={item.onClick}
              className="text-primary hover:opacity-80 hover:underline transition-colors"
            >
              {item.label}
            </button>
          ) : (
            <span className="text-gray-600">{item.label}</span>
          )}
          {index < items.length - 1 && (
            <ChevronRight className="w-4 h-4 text-gray-400" />
          )}
        </div>
      ))}
    </nav>
  )
}
